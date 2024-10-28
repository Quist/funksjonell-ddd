# Domene Dreven Design - med funksjonelle briller 👓

## Agenda
1. Introduksjon til DDD (se presentasjon)
2. Introduksjon til DDD med funksjonelle briller på
3. Intro til workshop

Etter å vært med på dette har du forhåpentligvis:
* Lært litt om DDD og hvilke fordeler en slik tankegang og arkitektur medbringer.
* Sett hvordan funksjonell tankegang går hand-i-hanske med workflow-prinsippet til DDD.
* Testet å kode litt både funksjonelt og domene-drevet

## Oppgaveutkast
1. Modelere simple typer
2. Modelere workflows som funksjoner

### Intro til oppgavene
Velkommen til Reodor Felgen – et teknisk system bygget for å holde sykkelentusiaster i gang! Her jobber vi med å ta imot ordrer fra sykkelfrelste kunder og sørger for at riktige deler sendes ut til rett tid. Gjennom et strømlinjeformet ordre- og leveringssystem håndterer vi alt fra bestilling til utsendelse av sykkeldeler som tannhjul, bremser og gir. Målet? Å bygge en robust, effektiv løsning som gjør det enklere for både kunder og logistikksystemet vårt å få sykkeldelene raskt på veien igjen!" 🚴‍♂️💻🚴‍♀️

#### Domenet oppsummert

Ulike kontekster i systemet (bounded contexts):

**Ordremottaking Kontekst** 

#### Oppgaver 📋
##### 1a
Reodor Felgen kjenner ikke til Int's og Strings. Modeller følgende konsepter:
* KundeId
* OrdreId
* Sykkeldel
* Mengde
* Kilomengde

Ved å modellere det på denne måten gjør vi det "umulig" å sende med en kundeId der det skulle være en ordreId og vica verca. Kompilatoren hjelper oss å holde styr på koden vår.

##### 1b
Placeholder