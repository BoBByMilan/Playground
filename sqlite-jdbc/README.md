# Banki alkalmazás szimulálása és elemzése

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![SQLite](https://img.shields.io/badge/Database-SQLite-blue) ![Status](https://img.shields.io/badge/Status-Prototype-green)

Ez a projekt egy banki informatikai rendszer modelljét valósítja meg
Java környezetben. A szoftver célja, hogy összehasonlítsa a hagyományos
szekvenciális adatfeldolgozást a modern párhuzamosítási technikákkal egy
számításigényes környezetben.

### ELŐSZÓ
*Nem végleges a terv bármilyen építő jellegű funkcióval, ötlettel bővülhet még.*


## A Projekt Célja
A banki rendszereknek egyszerre kell kezelniük a hatalmas adatmennyiséget
és a bonyolult biztonsági elemzéseket.
* **Adatkezelés:** Hogy kezeljünk nagy adatbázist?
* **Szimuláció:** Valós környezet szimulálása(tranzakciók,számlák zárolása, stb.)
* **Esetleg:** Csalás észlelés

## Technológiai Felépítés

A rendszer négy fő technológiai rétegre épül:

### 1. Adatbázis és Perzisztencia (IO Réteg)
* **Technológia:** SQLite, JDBC (Native)
* **Funkció:** Relációs adatmodell (Users, Accounts, Merchants, Transactions) tervezése.
* **Képesség:** Konzisztens, referenciális integritást megőrző 
tesztadat-generálás (Batch Processing) nagy mennyiségben.

### 2. High Performance Computing (CPU Réteg)
* **Technológia:** Java Parallel Streams, ForkJoinPool
* **Funkció:** Egy CPU-igényes kockázatelemző (Risk Scoring) algoritmus futtatása.
* **Mérés:** A szekvenciális és párhuzamos futási idők összevetése
és a gyorsulás (Speedup) elemzése.

### 3. Valós Idejű Szimuláció (GUI Réteg)
* **Technológia:** JavaFX
* **Funkció:** Aszinkron háttérszálakon (Background Threads)
futó tranzakció-generátor, amely valós időben terheli a rendszert, miközben a felület próbál reszponzív maradni.

### 4. GPU Gyorsítás (Kísérleti Réteg)
* **Technológia:** Aparapi 
* **Koncepció:** GPU-ra való java bájtkód fordítás 



## Futtatás

### Előfeltételek
* Java Development Kit (JDK) 17 vagy újabb
* SQLite JDBC Driver (a projekt dependencies része)

### Telepítés és Indítás
1. Klónozd a repót vagy töltsd le a forráskódot.
2. Nyisd meg IntelliJ-ben.
3. Futtasd a `Main.java` fájlt az adatbázis inicializálásához.
    * *Megjegyzés:* Az első futtatáskor a rendszer legenerálja
   a `playground.db` fájlt. Ez eltarthat 10-20 másodpercig. Van egy sor kikommentlve `[line:19]` ezt egyszer le kell futtani.
   * Mérések tervben!
   * Grafikus felület terben!

## Szerző
**[Gyulai Milán]** Projekt - 2025