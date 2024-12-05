#run_backtest.py
from data_fetcher import calculate_all_indicators
from backtest import fetch_coin_data, backtest_strategy
from strategy import btc_strategy, eth_strategy, avax_strategy, sol_strategy, render_strategy, fet_strategy

# Coin stratejilerini belirle
coin_strategileri = {
    "BTCUSDT": btc_strategy,
    "ETHUSDT": eth_strategy,
    "AVAXUSDT": avax_strategy,
    "SOLUSDT": sol_strategy,
    "RENDERUSDT": render_strategy,
    "FETUSDT": fet_strategy,
}

# Her coin için farklı time interval'lerde backtest yapalım
intervals = ["1d", "4h", "1h", "15m"]  # 1 günlük, 4 saatlik, 1 saatlik, 15 dakikalık

for coin, strategy_fn in coin_strategileri.items():
    for interval in intervals:
        print(f"Running backtest for {coin} on {interval} interval...")
        df = fetch_coin_data(coin, interval, days=120)  # 120 günlük veriyi çek
        df = calculate_all_indicators(df)  # İndikatörleri hesapla
        result = backtest_strategy(df, strategy_fn)  # Strateji fonksiyonunu geçir
        
        # Sonuçları yazdır
        print(f"Results for {coin} on {interval} interval:")
        print(f"Final Balance: ${result['final_balance']:.2f}")
        print(f"Profit: ${result['profit']:.2f}")
        print(f"Total Trades: {result['trades']}")
        print("Log:")
        for log in result['log']:
            print(log)
        print("=" * 50)
