# data_fetcher.py
from binance_api import get_market_price
from config import COINS
import pandas as pd
import numpy as np
import ta

def fetch_all_prices():
    """Tüm coinler için fiyatları getir."""
    market_data = {}
    for coin in COINS:
        data = get_market_price(coin)
        if "error" not in data:
            market_data[coin] = data["price"]
        else:
            market_data[coin] = "Error fetching price"
    return market_data

def process_historical_data(raw_data):
    """Binance'ten gelen ham veriyi işleyerek DataFrame'e dönüştürür."""
    df = pd.DataFrame(raw_data, columns=["open_time", "open", "high", "low", "close", "volume", 
                                         "close_time", "quote_asset_volume", "trades", 
                                         "taker_buy_base", "taker_buy_quote", "ignore"])

    # Veriyi sayısal türlere dönüştür
    df["close"] = pd.to_numeric(df["close"], errors="coerce")  # 'close' sütununu float'a dönüştür
    df["open"] = pd.to_numeric(df["open"], errors="coerce")  # 'open' sütununu float'a dönüştür
    df["high"] = pd.to_numeric(df["high"], errors="coerce")  # 'high' sütununu float'a dönüştür
    df["low"] = pd.to_numeric(df["low"], errors="coerce")  # 'low' sütununu float'a dönüştür
    df["volume"] = pd.to_numeric(df["volume"], errors="coerce")  # 'volume' sütununu float'a dönüştür

    # 'ignore' gibi gereksiz sütunları kaldırabilirsiniz, eğer kullanmıyorsanız
    df = df.drop(columns=["ignore"])

    # Zamanı okunabilir formata çevir
    df["open_time"] = pd.to_datetime(df["open_time"], unit="ms")  
    return df

def calculate_rsi(df, window=14):
    """RSI hesaplama."""
    rsi_indicator = ta.momentum.RSIIndicator(df["close"], window=window)
    df["rsi"] = rsi_indicator.rsi()
    return df

def calculate_sma(df, window=14):
    """SMA hesaplama."""
    sma_indicator = ta.trend.SMAIndicator(df["close"], window=window)
    df[f"sma_{window}"] = sma_indicator.sma_indicator()
    return df

def calculate_ema(df, window=14):
    """EMA hesaplama."""
    ema_indicator = ta.trend.EMAIndicator(df["close"], window=window)
    df[f"ema_{window}"] = ema_indicator.ema_indicator()
    return df

def calculate_bollinger_bands(df, window=20):
    """Bollinger Bands hesaplama."""
    bb_indicator = ta.volatility.BollingerBands(df["close"], window=window)
    df["bollinger_hband"] = bb_indicator.bollinger_hband()
    df["bollinger_lband"] = bb_indicator.bollinger_lband()
    return df

def calculate_macd(df, short_window=12, long_window=26, signal_window=9):
    """MACD hesaplama."""
    macd_indicator = ta.trend.MACD(df["close"], window_slow=long_window, window_fast=short_window, window_sign=signal_window)
    df["macd"] = macd_indicator.macd()
    df["macd_signal"] = macd_indicator.macd_signal()
    return df

def calculate_vwap(df):
    """VWAP hesaplama."""
    vwap_indicator = ta.volume.VolumeWeightedAveragePrice(df["high"], df["low"], df["close"], df["volume"])
    df["vwap"] = vwap_indicator.volume_weighted_average_price()
    return df

def calculate_cci(df, window=20):
    """CCI hesaplama."""
    cci_indicator = ta.trend.CCIIndicator(df["high"], df["low"], df["close"], window=window)
    df["cci"] = cci_indicator.cci()
    return df

def calculate_all_indicators(df):
    """Tüm indikatörleri hesapla."""
    df = calculate_rsi(df)
    df = calculate_sma(df, window=50)  # 50 günlük SMA
    df = calculate_sma(df, window=200) # 200 günlük SMA
    df = calculate_ema(df, window=20)  # 20 günlük EMA
    df = calculate_ema(df, window=50)  # 50 günlük EMA
    df = calculate_bollinger_bands(df)
    df = calculate_macd(df)
    df = calculate_vwap(df)
    df = calculate_cci(df)
    return df


