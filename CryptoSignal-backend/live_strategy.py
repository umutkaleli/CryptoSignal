#live_strategy.py

# Parameters
stop_loss_percentage = 0.02  # %2 stop loss
take_profit_percentage = 0.05  # %5 take profit

def calculate_stop_loss(entry_price):
    """Stop loss seviyesini hesapla."""
    return entry_price * (1 - stop_loss_percentage)

def calculate_take_profit(entry_price):
    """Take profit seviyesini hesapla."""
    return entry_price * (1 + take_profit_percentage)

def btcusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_lband = indicators.get("bollinger_lband", df['bollinger_lband'].iloc[i])
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        cci_threshold = indicators.get("cci", -100)
        rsi_l = indicators.get("rsi_l", 40)
        rsi_h = indicators.get("rsi_h", 65)

        # Alım sinyali
        if (
            current_price <= bollinger_lband
            and df['cci'].iloc[i] < cci_threshold
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            df['rsi'].iloc[i] > rsi_h
            and current_price >= bollinger_hband
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals


def ethusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_lband = indicators.get("bollinger_lband", df['bollinger_lband'].iloc[i])
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        rsi_l = indicators.get("rsi_l", 40)
        rsi_h = indicators.get("rsi_h", 70)

        # Alım sinyali
        if (
            current_price <= bollinger_lband
            and current_price < df['vwap'].iloc[i]
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            current_price >= bollinger_hband
            and df['rsi'].iloc[i] > rsi_h
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals


def avaxusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_lband = indicators.get("bollinger_lband", df['bollinger_lband'].iloc[i])
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        cci_threshold = indicators.get("cci", -100)
        rsi_l = indicators.get("rsi_l", 40)
        rsi_h = indicators.get("rsi_h", 65)

        # Alım sinyali
        if (
            current_price <= bollinger_lband
            and df['cci'].iloc[i] < cci_threshold
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            current_price >= bollinger_hband
            and df['rsi'].iloc[i] > rsi_h
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals


def solusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        rsi_l = indicators.get("rsi_l", 50)
        rsi_h = indicators.get("rsi_h", 65)

        # Alım sinyali
        if (
            current_price > df['ema_20'].iloc[i]
            and df['macd'].iloc[i] > df['macd_signal'].iloc[i]
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            current_price >= bollinger_hband
            and df['rsi'].iloc[i] > rsi_h
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals


def renderusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        rsi_l = indicators.get("rsi_l", 65)
        rsi_h = indicators.get("rsi_h", 70)

        # Alım sinyali
        if (
            current_price > df['ema_20'].iloc[i]
            and df['macd'].iloc[i] > df['macd_signal'].iloc[i]
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            current_price >= bollinger_hband
            and df['rsi'].iloc[i] > rsi_h
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals


def fetusdt_strategy(df, indicators):
    buy_signals = []
    sell_signals = []

    for i in range(len(df)):
        current_price = df['close'].iloc[i]

        # Kullanıcı tarafından gönderilen eşik değerleri
        bollinger_lband = indicators.get("bollinger_lband", df['bollinger_lband'].iloc[i])
        bollinger_hband = indicators.get("bollinger_hband", df['bollinger_hband'].iloc[i])
        rsi_l = indicators.get("rsi_l", 30)
        rsi_h = indicators.get("rsi_h", 70)

        # Alım sinyali
        if (
            current_price <= bollinger_lband
            and current_price <= df['vwap'].iloc[i]
            and df['rsi'].iloc[i] < rsi_l
        ):
            buy_signals.append(i)

        # Satış sinyali
        elif (
            current_price >= bollinger_hband
            and current_price >= df['vwap'].iloc[i]
            and df['rsi'].iloc[i] > rsi_h
        ):
            sell_signals.append(i)

    return buy_signals, sell_signals

