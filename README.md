# Domene Dreven Design - med funksjonelle briller 👓
Workshop for faggruppen Arkitektur i Praksis 2024. Etter å vært med på dette har du:
* Lært om DDD og hvilke fordeler en slik tankegang og arkitektur gir.
* Sett hvordan funksjonell tankegang passer godt sammen med workflow-prinsippet til DDD.
* Testet å kode litt både funksjonelt og domene-drevet

## Agenda
1. Introduksjon til DDD (Henrik)
2. Domenedreven design made functional (Joakim)
3. Intro til workshop (Joakim)

## 💡 Introduksjon
Vi har fått et spennende oppdrag fra ingen ringere enn Magnus Midtpø. Magnus har nemlig besluttet å åpne en nettbutikk som skal selge klatreutstyr og T-skjorter – alt designet for å inspirere til å klatre høyere og sikrere!

Etter å ha kjørt en Event Storming sammen med Magnus har dere funnet ut av:

* Ikke validerte bestillinger blir sendt inn via Youtube kommentarer på videoene til Magnus.
* Alle bestillinger må valideres før de behandles.
* Når en bestilling er validert skal den prissettes.
* Deretter skal den sendes til fraktavdelingen, faktura skal sendes til fakturaavdelingen og kunden skal få en bekreftelse.

```mermaid
---
title: Bestillingskontekst
---
flowchart LR
    UvalidertBestilling[Uvalidert Bestilling]
    Validate[Valider]
    Pris[Pris bestilling]
    Bekreft[Bekreft bestilling]
    OnFailure([Ved feil: Feilliste])
    Sideeffekter["`Sideffekter til andre Bounded Contexts:
    - Bestilling bekreftet til kunde
    - Bestilling sendt til fraktavdelingen
    - Bestilling sendt til fakturaavdeling`"]
    OnSuccess["`Suksess:
    Bestillingsbekreftelse sendt
    Bestilling plassert
    FakturerbarBestilling lassert`"]
    
    UvalidertBestilling --> Validate
    subgraph Bestillingskontekst
        Validate --> Pris
        Pris --> Bekreft
    end
    Bekreft --> Sideeffekter
    Bekreft --> OnSuccess
    Bekreft --> OnFailure
```

### Ressurser
Lenke til Result-biblioteket, andre relevante lenker.

## 👩‍💻 Oppgaver

### Kom i gang
Bruk gjerne intelliJ.
Klon osv.

2. Modelere simple typer
2. Modelere workflows som funksjoner


### Oppgaver 📋
Magnus Midtpø kjenner ikke til Integers og Strings. Vi har derfor modellert ordreId som en egen type.

#### Oppgave 1 - Domenemodellering med typer

> Ved å modellere det på denne måten gjør vi det "umulig" å sende med en kundeId der det skulle være en ordreId og vica verca. Kompilatoren hjelper oss å holde styr på koden vår.

##### 1a Definere en enkel value type
Kjør enhetstesten. Den feiler fordi KundeId ikke er definert som en type. Definer den

##### 1b Forretningsprosesser modelert som workflows
Implementer/endre på en av de første workflowen.

##### Oppgave 2 - Integritet og konsistens i Domenet
> Målet er å lage en avgrenset kontekst (bounded context) der all data inne i domenet er gyldig og konsistent, til forskjell fra dataen fra den skumle utenfor verden.

> Hvis vi kan være sikker på at all data er gyldig i vår kontekst, kan implementasjonen være mye renere og vi kan unngå defansiv koding.

##### Oppgave 2a - Integritet 
Magnus har nevnt at enhetsmengde alltid skal være mellom 1 og 1000. Implementer en integritetssjekk som gjør det umulig at en ordre kan inneholde mer enn 1000 enheter. 

##### Oppgave 2b - Konsistens
Alle validerteBestillinger må ha minst en ordrelinje. Hvordan kan vi modellere domenet for å sørge for dette? Prøv å løs dette ved å endre på modellen (og konstruktører).

> Hint: Sjekk ut NonEmptyList typen som ligger under `utils/`

##### Oppgave 2x Implementere forretningsregler ved å bruke typesystemet.
TODO: Vurdere oppgave med hvordan modellere uverifiserte og verifiserte adresser. 


#### Oppgave X -Videreutvikle designet 
Vi ser på 4 forskjellige typer endringer:
* Legge til et nytt steg i workflowen.
* Legge til input i workflowen.
* Endre definisjonen av en kjernetype og se hvordan det påvirker systemet.
* Endre hele arbeidsflyten til å stemme med forretningregler. 

#### Oppgave X_1 Legge til fraktkostnader

#### Oppgave X_2 Legge til støtte for VIP-kunder

#### Oppgave X_3 Legge til støtte for promokoder

#### Oppgave X_4 Implementere søndagsstengt

