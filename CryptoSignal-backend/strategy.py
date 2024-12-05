#strategy.py

# Parameters
stop_loss_percentage = 0.02  # %2 stop loss
take_profit_percentage = 0.05  # %5 take profit

def calculate_stop_loss(entry_price):
    """Stop loss seviyesini hesapla."""
    return entry_price * (1 - stop_loss_percentage)

def calculate_take_profit(entry_price):
    """Take profit seviyesini hesapla."""
    return entry_price * (1 + take_profit_percentage)

def btc_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None
    stop_loss = None
    take_profit = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Alım sinyali (Bollinger alt bant, CCI ve RSI uyumu)
        if (
            current_price <= df['bollinger_lband'].iloc[i]  # Bollinger alt bandı
            and df['cci'].iloc[i] < -100  # CCI düşük
            and df['rsi'].iloc[i] < 40  # RSI düşük
        ):
            if position is None:  # Pozisyon yoksa alım yap
                buy_signals.append(i)
                entry_price = current_price
                stop_loss = calculate_stop_loss(entry_price)
                take_profit = calculate_take_profit(entry_price)
                position = "LONG"

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Normal Satış Sinyali (RSI yüksek)
        elif (
            df['rsi'].iloc[i] > 70 and current_price >= df['bollinger_hband'].iloc[i]
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None
                stop_loss = None
                take_profit = None

    return buy_signals, sell_signals




def eth_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None
    stop_loss = None
    take_profit = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Alım sinyali (VWAP, Bollinger alt bant ve RSI uyumu)
        if (
            current_price <= df['bollinger_lband'].iloc[i]  # Bollinger alt bandı
            and current_price < df['vwap'].iloc[i]  # VWAP desteği
            and df['rsi'].iloc[i] < 40  # RSI düşük (satış baskısı azalmış)
        ):
            if position is None:  # Pozisyon yoksa alım yap
                buy_signals.append(i)
                entry_price = current_price
                stop_loss = calculate_stop_loss(entry_price)
                take_profit = calculate_take_profit(entry_price)
                position = "LONG"

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Normal Satış Sinyali (Fiyat Bollinger üst bandına ulaşıyor)
        elif (
            current_price >= df['bollinger_hband'].iloc[i]  # Bollinger üst bandı
            and df['rsi'].iloc[i] > 70  # RSI yüksek (aşırı alım)
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None
                stop_loss = None
                take_profit = None

    return buy_signals, sell_signals



# AVAX Strategy
def avax_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None
    stop_loss = None
    take_profit = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Alım sinyali (CCI ve Bollinger alt bant uyumu)
        if (
            current_price <= df['bollinger_lband'].iloc[i]
            and df['cci'].iloc[i] < -100  # CCI düşük
            and df['rsi'].iloc[i] < 40  # RSI düşük
        ):
            if position is None:
                buy_signals.append(i)
                entry_price = current_price
                stop_loss = calculate_stop_loss(entry_price)
                take_profit = calculate_take_profit(entry_price)
                position = "LONG"

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Normal Satış Sinyali
        elif (
            current_price >= df['bollinger_hband'].iloc[i]
            and df['rsi'].iloc[i] > 65
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None
                stop_loss = None
                take_profit = None

    return buy_signals, sell_signals


def sol_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None
    stop_loss = None
    take_profit = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Alım sinyali (EMA20, MACD ve RSI uyumu)
        if (
            current_price > df['ema_20'].iloc[i]  # EMA20'nin üstünde (trend pozitif)
            and df['macd'].iloc[i] > df['macd_signal'].iloc[i]  # MACD pozitif
            and df['rsi'].iloc[i] < 50  # RSI düşük
        ):
            if position is None:  # Pozisyon yoksa alım yap
                buy_signals.append(i)
                entry_price = current_price
                stop_loss = calculate_stop_loss(entry_price)
                take_profit = calculate_take_profit(entry_price)
                position = "LONG"

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Normal Satış Sinyali (Fiyat Bollinger üst bandına ulaşıyor)
        elif (
            current_price >= df['bollinger_hband'].iloc[i]  # Bollinger üst bandı
            and df['rsi'].iloc[i] > 65  # RSI yüksek (aşırı alım)
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None
                stop_loss = None
                take_profit = None

    return buy_signals, sell_signals



def render_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None
    stop_loss = None
    take_profit = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Aniden yükselen fiyatlar için belirli bir seviyede alım yap
        if (
            current_price > df['ema_20'].iloc[i]  # EMA20'nin üstünde (trend güçlü)
            and df['macd'].iloc[i] > df['macd_signal'].iloc[i]  # MACD pozitif
            and df['rsi'].iloc[i] < 65  # RSI aşırı alım seviyesine yaklaşmamış
        ):
            if position is None:  # Eğer pozisyon yoksa, alım yap
                buy_signals.append(i)
                entry_price = current_price
                stop_loss = calculate_stop_loss(entry_price)
                take_profit = calculate_take_profit(entry_price)
                position = "LONG"

        # Stop Loss kontrolü
        elif position == "LONG" and current_price < stop_loss:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Take Profit kontrolü
        elif position == "LONG" and current_price > take_profit:
            sell_signals.append(i)
            position = None
            stop_loss = None
            take_profit = None

        # Normal Satış Sinyali (Fiyatın fazla yükselmesi)
        elif (
            current_price >= df['bollinger_hband'].iloc[i]  # Bollinger üst bandının üstü
            and df['macd'].iloc[i] < df['macd_signal'].iloc[i]  # MACD negatif
            and df['rsi'].iloc[i] > 70  # RSI aşırı alım seviyesinde
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None
                stop_loss = None
                take_profit = None

    return buy_signals, sell_signals

# FET Strategy
def fet_strategy(df):
    buy_signals = []
    sell_signals = []
    position = None
    entry_price = None

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Alım sinyali (VWAP destek ve Bollinger alt bant uyumu)
        if (
            current_price <= df['bollinger_lband'].iloc[i]
            and current_price <= df['vwap'].iloc[i]
            and df['rsi'].iloc[i] < 30
        ):
            if position is None:
                buy_signals.append(i)
                entry_price = current_price
                position = "LONG"

        # Satış sinyali (VWAP direnç ve Bollinger üst bant uyumsuzluk)
        elif (
            current_price >= df['bollinger_hband'].iloc[i]
            and current_price >= df['vwap'].iloc[i]
            and df['rsi'].iloc[i] > 70
        ):
            if position == "LONG":
                sell_signals.append(i)
                position = None

    return buy_signals, sell_signals
