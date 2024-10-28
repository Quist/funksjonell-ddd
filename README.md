# Domene Dreven Design - med funksjonelle briller 👓

## Agenda
1. Introduksjon til DDD (se presentasjon)
2. Introduksjon til DDD med funksjonelle briller på
3. Intro til oppgaven

## Oppgaveutkast
1. Modelere simple typer
   2. KundeId



### Oppgaver (WIP)
Velkommen til Reodor Felgen – et teknisk system bygget for å holde sykkelentusiaster i gang! Her jobber vi med å ta imot ordrer fra sykkelfrelste kunder og sørger for at riktige deler sendes ut til rett tid. Gjennom et strømlinjeformet ordre- og leveringssystem håndterer vi alt fra bestilling til utsendelse av sykkeldeler som tannhjul, bremser og gir. Målet? Å bygge en robust, effektiv løsning som gjør det enklere for både kunder og logistikksystemet vårt å få sykkeldelene raskt på veien igjen!" 🚴‍♂️💻🚴‍♀️

#### Modelere domenet
Vi starter lett, modelering av "Simple Values". Sjekk oppgaveteksten for hjelp.

##### 1a
Reodor Felgen kjenner ikke til Int's og Strings. Modeller følgende konsepter:
* KundeId
* OrdreId
* Sykkeldel
* Mengde
* Kilomengde

Ved å modellere det på denne måten gjør vi det "umulig" å sende med en kundeId der det skulle være en ordreId og vica verca. Kompilatoren hjelper oss å holde styr på koden vår.

##### 1b
