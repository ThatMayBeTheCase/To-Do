# To-Do

En enkel "att göra" lista där du kan skapa uppgifter, markera dem som klara och ta bort dem. Listan kan filtreras på aktiva eller klara uppgifter och sparas i MySQL.

Backend är byggd med Java och Spring Boot. Frontend använder HTML, CSS och JavaScript. Appen körs i Docker och deployas automatiskt till min server genom GitHub Actions.

Appen har ingen inloggning. Frontend använder en gemensam standardanvändare och kategori; personliga konton kan bli en senare förbättring.

## Appen på servern

[Öppna To-Do](https://casemint.tail3bd46a.ts.net)

Appen är tillgänglig över HTTPS via Tailscale Funnel.

## Starta lokalt

Du behöver Docker. Hämta projektet som ZIP eller klona det med Git:

```bash
git clone https://github.com/ThatMayBeTheCase/To-Do.git
cd To-Do
```

Kör sedan från projektmappen:

```bash
docker compose up -d --build
```

Öppna [http://localhost:8080](http://localhost:8080) när appen har startat. Första starten tar lite längre tid eftersom images behöver hämtas och databasen skapas. Java byggs i Docker och behöver inte installeras separat.

Stoppa appen och databasen med `docker compose down`. Sparade uppgifter finns kvar i en Docker-volym. Om något inte startar kan du läsa loggarna med `docker compose logs -f app mysql`.

## CI/CD och arbetssätt

Ändringar görs på separata brancher och går genom pull requests till `dev`. När en version är redo mergas `dev` till `main`, vilket startar deploymenten.

[CI-workflowen](.github/workflows/ci.yml) bygger och kör Java-testerna vid varje push och vid pull requests till `dev` och `main`.

[Deployment-workflowen](.github/workflows/deploy.yml) kör testerna, bygger en Docker-image och publicerar den till Docker Hub. Därefter ansluter den till servern via Tailscale SSH och startar versionen för aktuell commit med Docker Compose. Till sist kontrolleras att startsidan svarar via HTTP.

Serverns databaslösenord ligger i en separat `.env`-fil och anslutningsuppgifterna för GitHub Actions i repository secrets. Den lokala Compose-filen använder enkla utvecklingslösenord.

Nästa förbättringar är att köra Playwright i CI och förbättra API:ets validering och felhantering.
