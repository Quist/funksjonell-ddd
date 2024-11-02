# Domene Dreven Design - med funksjonelle briller 👓
Workshop for faggruppen Arkitektur i praksis 2024.

Etter å vært med på dette har du forhåpentligvis:
* Lært litt om DDD og hvilke fordeler en slik tankegang og arkitektur medbringer.
* Sett hvordan funksjonell tankegang går hand-i-hanske med workflow-prinsippet til DDD.
* Testet å kode litt både funksjonelt og domene-drevet

## Agenda
1. Introduksjon til DDD (Henrik)
2. Domenedreven design made functional (Joakim)
3. Intro til workshop

## 👩‍💻 Oppgaver
1. Modelere simple typer
2. Modelere workflows som funksjoner

### 💡 Introduksjon
Vi har fått et spennende oppdrag fra ingen ringere enn Magnus Midtpø. Magnus har nemlig besluttet å åpne en nettbutikk som skal selge klatreutstyr og T-skjorter – alt designet for å inspirere til å klatre høyere og sikrere!

Men, som alt annet med Magnus, er dette ikke en helt standard butikk. Kundene hans sender inn uvaliderte bestillinger via YouTube Forms, og vårt system må derfor takle en jevn strøm av potensielt “kreative” ordredetaljer. Vårt oppdrag, som Magnus har så diplomatisk beskrevet det, er å sørge for at ordrene faktisk kan behandles og konverteres til noe som kan sendes ut av lageret.

Så nå setter vi kursen mot å bygge en solid, fleksibel løsning for vår eventyrlystne klatrekonsulent. Klar til å henge i tauet og få Magnus til toppen – på en funksjonell og domenedrevet måte?

![](https://files.oaiusercontent.com/file-CdsPb5yyLyyWBoxR5aIty4tB?se=2024-10-29T23%3A32%3A07Z&sp=r&sv=2024-08-04&sr=b&rscc=max-age%3D604800%2C%20immutable%2C%20private&rscd=attachment%3B%20filename%3D05a0f24b-d073-492b-b171-a61741cb9c05.webp&sig=6hsHBthjSE7evKdTFGKwV1CzrvixuennFkdg4T1kzQc%3D)


#### Domenet oppsummert

Ulike kontekster i systemet (bounded contexts):

**Ordremottaking Kontekst** 

### Oppgaver 📋
Magnus Midtpø kjenner ikke til Integers og Strings. Vi har derfor modellert ordreId som en egen type.

#### Oppgave 1 - Domenemodellering med typer

> Ved å modellere det på denne måten gjør vi det "umulig" å sende med en kundeId der det skulle være en ordreId og vica verca. Kompilatoren hjelper oss å holde styr på koden vår.

##### 1a Definere en enkel value type
Kjør enhetstesten. Den feiler fordi KundeId ikke er definert som en type. Definer den

##### 1b Foretningsprosesser modelert som workflows
Implementer/endre på en av de første workflowen.

##### Oppgave 2 - Integritet og konsistens i Domenet
![](https://files.oaiusercontent.com/file-cjoghcz5ZDLRF4KzJxzWdkv4?se=2024-10-29T23%3A00%3A42Z&sp=r&sv=2024-08-04&sr=b&rscc=max-age%3D604800%2C%20immutable%2C%20private&rscd=attachment%3B%20filename%3Db3c0c44b-fbdc-4325-8df1-3a2a7d3e1c53.webp&sig=ffonMiPlCJRUHcjjz1z%2B1bJ1lTEhv24cRNmoBGv2M9Y%3D)
> Målet er å lage en avgrenset kontekst (bounded context) der all data inne i domenet er gyldig og konsistent, til forskjell fra dataen fra den skumle utenfor verden.

> Hvis vi kan være sikker på at all data er gyldig i vår kontekst, kan implementasjonen være mye renere og vi kan unngå defansiv koding.

##### Oppgave 2a - Integritet 
Magnus har nevnt at enhetsmengde alltid skal være mellom 1 og 1000. Implementer en integritetssjekk som gjør det umulig at en ordre kan inneholde mer enn 1000 enheter. 

##### Oppgave 2b - Konsistens
Alle validerteBestillinger må ha minst en ordrelinje. Hvordan kan vi modellere domenet for å sørge for dette? Prøv å løs dette ved å endre på modellen (og konstruktører).

> Hint: Sjekk ut NonEmptyList typen som ligger under `utils/`

##### Oppgave 2x Implementere forettningsregler ved å bruke typesystemet.
TODO: Vurdere oppgave med hvordan modellere uverifiserte og verifiserte adresser. 