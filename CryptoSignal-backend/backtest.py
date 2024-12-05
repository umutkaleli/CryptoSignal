from data_fetcher import process_historical_data, calculate_all_indicators
from binance_api import get_historical_klines

# Parameters
stop_loss_percentage = 0.02  # %2 stop loss
take_profit_percentage = 0.05  # %5 take profit


def calculate_stop_loss(entry_price):
    """Stop loss seviyesini hesapla."""
    return entry_price * (1 - stop_loss_percentage)


def calculate_take_profit(entry_price):
    """Take profit seviyesini hesapla."""
    return entry_price * (1 + take_profit_percentage)


def fetch_coin_data(symbol, interval, days=120):
    """Binance API'den veriyi çeker ve işleyerek DataFrame'e dönüştürür."""
    raw_data = get_historical_klines(symbol, interval, days=days)
    df = process_historical_data(raw_data)
    df_with_indicators = calculate_all_indicators(df)
    return df_with_indicators


def backtest_strategy(df, strategy_fn):
    """Backtest işlemi."""
    initial_balance = 10000.0  # Başlangıç bakiyesi
    cash_balance = initial_balance  # Güncel nakit bakiye
    coin_balance = 0.0  # Alınan coin miktarı
    position = None  # Pozisyon durumu: None, "LONG"
    entry_price = None
    stop_loss = None
    take_profit = None

    buy_signals, sell_signals = strategy_fn(df)
    results = []  # İşlem loglarını tutar

    for i in range(len(df)):
        current_price = df['close'].iloc[i]
        current_time = df['open_time'].iloc[i]

        # Alım sinyali kontrolü
        if i in buy_signals and position is None and cash_balance > 0:
            entry_price = current_price
            stop_loss = calculate_stop_loss(entry_price)
            take_profit = calculate_take_profit(entry_price)
            coin_balance = cash_balance / entry_price  # Tüm nakit ile coin al
            cash_balance = 0.0  # Nakit bakiye sıfırlandı
            position = "LONG"
            results.append(f"Buy at {current_time}: {entry_price:.2f}, Coins: {coin_balance:.6f}")

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            cash_balance = coin_balance * current_price  # Coinleri sat
            loss = cash_balance - (coin_balance * entry_price)  # İşleme özel kar/zarar
            coin_balance = 0.0  # Coin miktarı sıfırlandı
            results.append(f"Stop Loss at {current_time}: {current_price:.2f}, Loss: {loss:.2f}")
            position = None
            entry_price = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            cash_balance = coin_balance * current_price  # Coinleri sat
            profit = cash_balance - (coin_balance * entry_price)  # İşleme özel kar
            coin_balance = 0.0  # Coin miktarı sıfırlandı
            results.append(f"Take Profit at {current_time}: {current_price:.2f}, Profit: {profit:.2f}")
            position = None
            entry_price = None
            stop_loss = None
            take_profit = None

        # Normal satış sinyali kontrolü
        elif i in sell_signals and position == "LONG":
            cash_balance = coin_balance * current_price  # Coinleri sat
            profit = cash_balance - (coin_balance * entry_price)  # İşleme özel kar
            coin_balance = 0.0  # Coin miktarı sıfırlandı
            results.append(f"Sell at {current_time}: {current_price:.2f}, Profit: {profit:.2f}")
            position = None
            entry_price = None
            stop_loss = None
            take_profit = None

    # Toplam bakiye: nakit + eldeki coin değeri
    total_balance = cash_balance + (coin_balance * df['close'].iloc[-1] if position == "LONG" else 0)
    total_profit = total_balance - initial_balance
    return {
        "final_balance": total_balance,
        "profit": total_profit,
        "trades": len(results),
        "log": results
    }
