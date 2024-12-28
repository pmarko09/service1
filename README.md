Zdefiniowany jest docker compose w service 1w celu odpalenia wszystkiego przy jego uzyciu nalezy wczesniej zbudowac obraz service2
oraz w docker compose wskazac jego nazwe.

W service 2 puszczamy: `docker build -t service2image .`

Następnie możemy już odpalić docker compose.

Service 1 korzysta z bazy mongoDB 

Service 2 z PostgreSQL