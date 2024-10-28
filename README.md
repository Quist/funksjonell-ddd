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
Vi har fått et spennende oppdrag fra ingen ringere enn Magnus Midtpø. Magnus har nemlig besluttet å åpne en nettbutikk som skal selge klatreutstyr og T-skjorter – alt designet for å inspirere til å klatre høyere og sikrere!

Men, som alt annet med Magnus, er dette ikke en helt standard butikk. Kundene hans sender inn uvaliderte bestillinger via YouTube Forms, og vårt system må derfor takle en jevn strøm av potensielt “kreative” ordredetaljer. Vårt oppdrag, som Magnus har så diplomatisk beskrevet det, er å sørge for at ordrene faktisk kan behandles og konverteres til noe som kan sendes ut av lageret.

Så nå setter vi kursen mot å bygge en solid, fleksibel løsning for vår eventyrlystne klatrekonsulent. Klar til å henge i tauet og få Magnus til toppen – på en funksjonell og domenedrevet måte?

#### Domenet oppsummert

Ulike kontekster i systemet (bounded contexts):

**Ordremottaking Kontekst** 

#### Oppgaver 📋
##### 1a
Magnus Midtpø kjenner ikke til Int's og Strings. Modeller følgende konsepter:
* KundeId
* OrdreId
* Sykkeldel
* Mengde
* Kilomengde

Ved å modellere det på denne måten gjør vi det "umulig" å sende med en kundeId der det skulle være en ordreId og vica verca. Kompilatoren hjelper oss å holde styr på koden vår.

##### 1b
Implementer en av de første workflowen.


