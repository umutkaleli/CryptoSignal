# app.py
from flask import Flask, jsonify, request
from flask_socketio import SocketIO, emit
from data_fetcher import process_historical_data, calculate_all_indicators,fetch_all_prices
from binance_api import get_historical_klines,get_market_price
from importlib import import_module
import threading
import json
import config
import time
from datetime import datetime

app = Flask(__name__)
app.config["SECRET_KEY"] = config.BINANCE_SECRET_KEY
socketio = SocketIO(app, cors_allowed_origins="*", async_mode="threading")


with open("wallet.json", "r") as f:
    wallet = json.load(f)


# Başlangıç bakiyesi
cash_balance = 10000.0  # Dolar cinsinden başlangıç bakiyesi

# Canlı botlar için global değişken
active_trading_threads = {}


# Coin'e göre strateji fonksiyonunu al
# Coin'e göre strateji fonksiyonunu al
def get_strategy_by_symbol(symbol):
    try:
        strategy_module = import_module("live_strategy")  # live_strategy dosyasını kullan
        strategy_fn_name = f"{symbol.lower()}_strategy"
        strategy_fn = getattr(strategy_module, strategy_fn_name, None)
        if not strategy_fn:
            raise AttributeError(f"Strategy '{strategy_fn_name}' not found in live_strategy module.")
        return strategy_fn
    except ImportError:
        raise ImportError("Could not import live_strategy module")
    except AttributeError as e:
        raise e


# Canlı işlem botu
def run_trading_bot(symbol, interval, indicators):
    global cash_balance, wallet

    print(f"Starting trading bot for {symbol}...")
    strategy_fn = get_strategy_by_symbol(symbol)
    active_trading_threads[symbol]['running'] = True  # Botun çalıştığını belirt

    # Mevcut durumları yükle
    position = "LONG" if wallet.get(symbol, 0.0) > 0 else None
    coin_balance = wallet.get(symbol, 0.0)
    entry_price = None
    stop_loss = None
    take_profit = None

    # Eğer pozisyon varsa entry_price'ı hesapla
    if position == "LONG" and coin_balance > 0:
        entry_price = 10000.0 / coin_balance if cash_balance == 0 else None
        if entry_price:
            stop_loss = entry_price * (1 - indicators.get("stop_loss", 0.02))
            take_profit = entry_price * (1 + indicators.get("take_profit", 0.05))
            print(f"Restored position: LONG with entry price {entry_price}, stop loss {stop_loss}, take profit {take_profit}")
        else:
            print("Error: Unable to calculate entry price while restoring position.")

    try:
        while active_trading_threads[symbol]['running']:
            print(f"Fetching data for {symbol}...")
            raw_data = get_historical_klines(symbol, interval, days=1)
            df = process_historical_data(raw_data)
            df_with_indicators = calculate_all_indicators(df)

            print("Running strategy...")
            buy_signals, sell_signals = strategy_fn(df_with_indicators, indicators)

            current_price = df_with_indicators["close"].iloc[-1]
            current_time = df_with_indicators["open_time"].iloc[-1]
            results = []

            # Alım sinyali
            if position is None and cash_balance > 0 and len(buy_signals) > 0:
                entry_price = current_price
                stop_loss = entry_price * (1 - indicators.get("stop_loss", 0.02))
                take_profit = entry_price * (1 + indicators.get("take_profit", 0.05))
                coin_balance = cash_balance / entry_price
                wallet[symbol] = coin_balance
                cash_balance = 0.0
                position = "LONG"
                results.append(f"BUY, Amount: {coin_balance:.6f}")
                print(f"Buy signal at {current_time}, price: {entry_price}")

            # Satış sinyali
            elif position == "LONG" and stop_loss and take_profit:
                if current_price < stop_loss:  # Stop Loss
                    cash_balance = coin_balance * current_price
                    wallet[symbol] = 0.0
                    results.append(f"Stop Loss")
                    coin_balance = 0.0
                    position = None
                    print(f"Stop Loss triggered at {current_time}, price: {current_price}")

                elif current_price > take_profit:  # Take Profit
                    cash_balance = coin_balance * current_price
                    wallet[symbol] = 0.0
                    results.append(f"Take Profit")
                    coin_balance = 0.0
                    position = None
                    print(f"Take Profit triggered at {current_time}, price: {current_price}")

                elif len(sell_signals) > 0:  # Normal Satış
                    cash_balance = coin_balance * current_price
                    wallet[symbol] = 0.0
                    results.append(f"SELL")
                    coin_balance = 0.0
                    position = None
                    print(f"Normal Sell triggered at {current_time}, price: {current_price}")

            # Hold sinyali
            if len(results) == 0:
                results.append(f"HOLD")
                print(f"Hold: {symbol} at price {current_price}")

            formatted_datetime = datetime.now().strftime("%Y-%m-%d %H:%M:%S")  # İstenilen formatta tarih ve saat
            # Frontend'e veri gönder
            socketio.emit("trade_update", {
                "symbol": symbol,
                "results": results,
                "balance": cash_balance,
                "coin_balance": coin_balance,
                "current_price": current_price,
                "timestamp": formatted_datetime  # Eklenen tarih ve saat bilgisi
            })

            time.sleep(60)
    except Exception as e:
        print(f"Error in bot for {symbol}: {str(e)}")
        socketio.emit("trade_error", {"error": str(e)})

    print(f"Bot for {symbol} stopped.")









@app.route("/start-bot", methods=["POST"])
def start_bot():
    data = request.get_json()
    symbol = data.get("symbol", "BTCUSDT")
    interval = data.get("interval", "1m")
    indicators = data.get("indicators", {})

    if symbol not in active_trading_threads or not active_trading_threads[symbol]['running']:
        active_trading_threads[symbol] = {
            'thread': threading.Thread(
                target=run_trading_bot,
                args=(symbol, interval, indicators),
                daemon=True
            ),
            'running': True
        }
        active_trading_threads[symbol]['thread'].start()
        return jsonify({"message": f"Bot started for {symbol}!"})
    else:
        return jsonify({"error": f"Bot for {symbol} is already running!"}), 400


# Bot durdurma endpoint'i
@app.route("/stop-bot", methods=["POST"])
def stop_bot():
    data = request.get_json()
    symbol = data.get("symbol", "BTCUSDT")

    if symbol in active_trading_threads and active_trading_threads[symbol]['running']:
        active_trading_threads[symbol]['running'] = False  # Botu durdur
        active_trading_threads[symbol]['thread'].join(timeout=2)  # Thread'i bekle
        del active_trading_threads[symbol]
        return jsonify({"message": f"Bot stopped for {symbol}!"})
    else:
        return jsonify({"error": f"No active bot for {symbol}!"}), 400









@app.route("/wallet", methods=["GET"])
def get_wallet():
    """Cüzdandaki tüm coinlerin fiyatlarını, miktarlarını ve toplam değeri döner."""
    global cash_balance
    response = []
    total_wallet_value = 0.0  # Coin'lerin toplam değeri

    for symbol, amount in wallet.items():
        price_data = get_market_price(symbol)
        if "error" not in price_data:
            price = float(price_data["price"])
            coin_value = price * amount
            total_wallet_value += coin_value
            response.append({
                "symbol": symbol,
                "price": price,
                "amount": amount,
                "total_value": coin_value  # Bu coinin toplam değeri
            })
        else:
            response.append({
                "symbol": symbol,
                "price": "N/A",
                "amount": amount,
                "total_value": 0.0,
                "error": price_data["error"]
            })

    # Toplam bakiye (coin değerleri + nakit bakiye)
    total_balance = cash_balance + total_wallet_value

    return jsonify({
        "cash_balance": cash_balance,  # Nakit bakiye
        "total_wallet_value": total_wallet_value,  # Coin'lerin toplam değeri
        "total_balance": total_balance,  # Genel toplam
        "details": response  # Her bir coin detayı
    })

@app.route("/coin", methods=["GET"])
def get_coin_data():
    """Belirli bir coin için fiyat ve cüzdan miktarını döner."""
    symbol = request.args.get("symbol", default=None, type=str)
    
    if not symbol:
        return jsonify({"error": "Symbol is required"}), 400

    if symbol not in wallet:
        return jsonify({"error": "Symbol not found in wallet"}), 404

    try:
        # Market fiyatını al
        price_data = get_market_price(symbol)
        if "error" in price_data:
            return jsonify({"error": price_data["error"]}), 400
        
        # Cüzdan miktarını ekle
        amount = wallet[symbol]
        response = {
            "symbol": symbol,
            "price": float(price_data["price"]),
            "amount": amount,
            "total_value": float(price_data["price"]) * amount  # Toplam değer
        }
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/reset", methods=["POST"])
def reset_balance():
    """Cüzdanı sıfırla ve başlangıç bakiyesini tekrar 10,000 dolar yap."""
    global cash_balance, wallet

    # Nakit bakiyeyi sıfırla
    cash_balance = 10000.0

    # Cüzdandaki tüm coin miktarlarını sıfırla
    for symbol in wallet:
        wallet[symbol] = 0.0

    return jsonify({
        "message": "Balance and wallet reset successfully!",
        "cash_balance": cash_balance,
        "wallet": wallet
    })

@app.route("/simulate", methods=["GET"])
def simulate_trading():
    """Simülasyon için GET isteği ile filtreleri al."""
    # URL parametrelerini al
    symbol = request.args.get("symbol", default="BTCUSDT", type=str)
    interval = request.args.get("interval", default="1m", type=str)
    
    try:
        # Binance'ten veri al
        raw_data = get_historical_klines(symbol, interval, days=1)  # Örnek: 1 günlük veri
        df = process_historical_data(raw_data)
        df_with_indicators = calculate_all_indicators(df)  # Yeni indikatör fonksiyonlarını kullan
        
        # Basit bir strateji simülasyonu
        results = {
            "symbol": symbol,
            "interval": interval,
            "data": df_with_indicators.tail(5).to_dict(orient="records")  # Son 5 veri
        }
        return jsonify(results)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route("/historical/<symbol>/<interval>/<days>", methods=["GET"])
def get_historical_data(symbol, interval, days):
    """Belirli bir coin için geçmiş veriyi döner."""
    try:
        days = int(days)
        historical_data = get_historical_klines(symbol, interval, days)
        return historical_data.to_json(orient="records")
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route("/prices", methods=["GET"])
def get_prices():
    """Tüm coin fiyatlarını döner."""
    prices = fetch_all_prices()
    return jsonify(prices)

@app.route("/get-candlestick", methods=["GET"])
def get_candlestick():
    """Mum grafiği verilerini döner."""
    symbol = request.args.get("symbol", default="BTCUSDT", type=str)
    interval = request.args.get("interval", default="1m", type=str)
    
    try:
        raw_data = get_historical_klines(symbol, interval, days=1)  # Örnek: 1 günlük veri
        df = process_historical_data(raw_data)
        candlestick_data = df[["open_time", "open", "high", "low", "close"]].tail(50).to_dict(orient="records")
        return jsonify(candlestick_data)
    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == "__main__":
    socketio.run(app, debug=True, host="127.0.0.1", port=5000)

