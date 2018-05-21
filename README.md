# Simple-Snake

###Rövid leírás:<br />
Kompetitív Snake játék, melyben egy kígyót irányítunk és ahogy felvesszük a véletlenszerűen megjelenő gyümölcsöket,hosszabb lesz a kígyónk.
A játékot billentyűzettel irányítjuk. Azért kompetitív, mert a pályán lesznek még rajtunk kívül gép által irányított kígyók, amiknek ugyanaz a céljuk mint nekünk.
Választható az ellenfelek száma és a nehézségi szint. A játék végén (amikor nekiütközünk valaminek) beírható a nevünk, és teljesítményünk alapján kapunk pontokat amiket egy ranglista fog tárolni.
A ranglista egy külső szerveren lesz egy adatbázisban. Ehhez elengedhetetlen a hálózati kapcsolat.

###Választott technológiák:<br />
*	Properties, preferences: felhasználó neve, beállításainak tárolása. 
*	JavaFX: grafikus felület kezelésére.
*	JDBC: ranglista adatbázisra.
*	Net: szerveralkalmazás a ranglistának.
<br />
Készült a BME-VIK Java-technológia tárgyhoz.
