# blood-monitor

0. Interfejs WWW jest dostępny pod (Chrome)

http://ec2-54-200-120-210.us-west-2.compute.amazonaws.com:8080/user/signin

Przykładowe dane do logowania to: jaro/jaro

1. Baza danych

PostgreSQL. Używaliśmy amazon RDS, ze względu na brak konieczności ręcznej instalacji.

2. Serwer

Dane do bazy danych są w pliku config.py

Aby uruchomić serwer należy wykonać polecenia:

~~~~~
$ sudo apt-get update
$ sudo apt-get install python3-pip
$ git clone https://github.com/put-iwm-2016-w/blood-monitor.git
$ cd blood-monitor/server
$ pip3 install -r requirements.txt 
$ cd src
$ python3 run.py
~~~~

Serwer będzie nasłuchiwał na porcie 8080

3. Klient

Dostępna jest binarka w katalogu głównym projektu. Projekt można skompilować przy użyciu Android Studio

