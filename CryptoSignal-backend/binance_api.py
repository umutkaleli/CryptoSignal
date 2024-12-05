#binance_api.py
import requests
from config import BINANCE_API_KEY, BINANCE_SECRET_KEY, BASE_URL
import pandas as pd
import time
from datetime import datetime, timedelta

# API başlıkları
headers = {
    "X-MBX-APIKEY": BINANCE_API_KEY
}

# Binance server zamanını al
def get_server_time():
    url = f"{BASE_URL}/v3/time"
    response = requests.get(url, headers=headers)
    return response.json()

def get_market_price(symbol):
    """Belirli bir coin için fiyat bilgisi al."""
    url = f"{BASE_URL}/v3/ticker/price?symbol={symbol}"
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        return {"error": response.text}

def get_historical_klines(symbol, interval, days):
    """
    Binance'ten belirli bir zaman diliminde (interval) geçmiş veriyi çeker.
    - symbol: Coin sembolü (ör: "BTCUSDT").
    - interval: Zaman aralığı (ör: "1d", "4h").
    - days: Çekilecek toplam gün sayısı.
    """
    end_time = int(time.time() * 1000)  # Şu anki zaman (milisaniye)
    start_time = int((datetime.now() - timedelta(days=days)).timestamp() * 1000)
    
    all_data = []
    while start_time < end_time:
        url = f"{BASE_URL}/v3/klines"
        params = {
            "symbol": symbol,
            "interval": interval,
            "startTime": start_time,
            "limit": 1000  # Maksimum 1000 veri
        }
        response = requests.get(url, headers=headers, params=params)
        if response.status_code == 200:
            data = response.json()
            if not data:  # Veri biterse döngüyü sonlandır
                break
            all_data.extend(data)
            # Son alınan verinin kapanış zamanını al ve bir ileriye ayarla
            start_time = data[-1][6]
        else:
            raise Exception(f"API Error: {response.text}")
    
    return pd.DataFrame(all_data, columns=[
        "open_time", "open", "high", "low", "close", "volume",
        "close_time", "quote_asset_volume", "number_of_trades",
        "taker_buy_base_asset_volume", "taker_buy_quote_asset_volume", "ignore"
    ])

def get_candlestick(symbol, interval, limit=10000):
    """
    Binance'ten mum grafiği verilerini alır ve işlenmiş bir DataFrame döndürür.
    - symbol: Coin sembolü (ör: "BTCUSDT").
    - interval: Zaman aralığı (ör: "1m", "1h", "1d").
    - limit: Çekilecek veri sayısı (maksimum 1000).
    """
    url = f"{BASE_URL}/v3/klines"
    params = {
        "symbol": symbol,
        "interval": interval,
        "limit": limit
    }
    response = requests.get(url, headers=headers, params=params)
    if response.status_code == 200:
        data = response.json()
        df = pd.DataFrame(data, columns=[
            "open_time", "open", "high", "low", "close", "volume",
            "close_time", "quote_asset_volume", "number_of_trades",
            "taker_buy_base_asset_volume", "taker_buy_quote_asset_volume", "ignore"
        ])
        # Verileri float'a dönüştür
        df["open"] = pd.to_numeric(df["open"])
        df["high"] = pd.to_numeric(df["high"])
        df["low"] = pd.to_numeric(df["low"])
        df["close"] = pd.to_numeric(df["close"])
        df["volume"] = pd.to_numeric(df["volume"])
        return df
    else:
        raise Exception(f"API Error: {response.text}")
