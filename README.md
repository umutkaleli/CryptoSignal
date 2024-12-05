# **Crypto Trading Bot**

Bu proje, **Kotlin** ile geliştirilmiş bir **frontend** ve **Python** ile yazılmış bir **backend** içeren bir kripto ticaret botu uygulamasıdır. Bot, Binance API'yi kullanarak gerçek zamanlı fiyat verilerini alır ve belirli stratejilere dayalı olarak alım-satım kararları verir. Ayrıca, indikatörler dinamik olarak güncellenebilir ve özelleştirilebilir.

---

## **Proje Özeti**

- **Frontend**: Kullanıcı arayüzü Kotlin ile yazılmıştır ve botun kontrolünü sağlar.
- **Backend**: Python ile yazılmış, Flask ve Socket.IO kullanılarak geliştirilmiştir. Gerçek zamanlı ticaret stratejileri ve indikatör hesaplamaları içerir.
- **Kripto Ticareti**: Binance API kullanılarak veri akışı sağlanır ve dinamik olarak belirlenen stratejilere göre alım-satım yapılır.

---

## **Özellikler**

1. **Dinamik Ticaret Stratejileri**  
   - Bollinger Bands, RSI, CCI, VWAP gibi indikatörleri içerir.
   - Kullanıcılar stratejilerini frontend üzerinden özelleştirebilir.

2. **Gerçek Zamanlı Veri İşleme**  
   - Binance API ile kripto fiyatları anlık olarak alınır.
   - İndikatör hesaplamaları backend'de yapılır.

3. **Başlat ve Durdur Özelliği**  
   - Bot kolayca başlatılabilir, durdurulabilir ve tekrar çalıştırılabilir.

4. **Gerçek Zamanlı Güncellemeler**  
   - Socket.IO kullanılarak frontend'e sürekli güncellemeler gönderilir (örneğin, alım/satım işlemleri, bakiyeler).

5. **Tarih ve Saat Desteği**  
   - Tüm işlemler UTC+3 zaman dilimine göre kaydedilir.

---

## **Kullanım**

### **Backend Çalıştırma**

1. **Gerekli Kütüphaneleri Yükleyin**  
   Python ortamını oluşturun ve bağımlılıkları yükleyin:

   ```bash
   python -m venv env
   source env/bin/activate  # Windows için: env\Scripts\activate
   pip install -r requirements.txt

2. **Backend'i Çalıştırın**
   **Flask sunucusunu başlatın:**
   ```bash
   python app.py

### **Frontend Çalıştırma**

1. **Kotlin Projesini Açın
   Android Studio veya IntelliJ IDEA kullanarak projeyi açın.

2. **Backend Bağlantısını Ayarlayın
   Backend'in çalıştığı IP ve port'u ayarlayın.

3. **Uygulamayı Çalıştırın
   Emulator veya fiziksel cihazda çalıştırarak frontend'i test edin.

## **API Endpoint'leri**

| **Metot** | **Endpoint**     | **Açıklama**                                         |
|-----------|------------------|-----------------------------------------------------|
| `POST`    | `/start-bot`     | Botu başlatır.                                      |
| `POST`    | `/stop-bot`      | Botu durdurur.                                      |
| `GET`     | `/wallet`        | Mevcut cüzdan bakiyesini ve coin miktarlarını döner. |
| `GET`     | `/simulate`      | Belirli parametreler ile backtest çalıştırır.        |
| `GET`     | `/prices`        | Tüm coin fiyatlarını döner.                         |
| `GET`     | `/coin`          | Belirtilen coin için cüzdan bilgilerini döner.       |
| `GET`     | `/historical`    | Geçmiş veri fiyatlarını döner.                      |
| `GET`     | `/get-candlestick` | Mum grafiği verilerini döner.                      |


## **Bağımlılıklar**

### **Backend**
- Python 3.8 veya üzeri
- Flask
- Flask-SocketIO
- Pandas
- NumPy
- Binance API

### **Frontend**
- Kotlin
- Retrofit
- Coroutine
- LiveData ve ViewModel

---

## **Geliştirilmesi Planlanan Özellikler**
- Daha fazla ticaret stratejisi entegrasyonu
- Kullanıcıların geçmiş performanslarını görüntüleyebilmesi
- Mobil uygulamada kar ve zarar analizleri
- Machine Learning kullanarak strateji optimizasyonu



# CryptoSignal
 
