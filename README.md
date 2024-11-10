# 👓 Domene Dreven Design - med funksjonelle briller 👓 
Workshop for faggruppen *Arkitektur i Praksis*. Etter å ha deltatt i denne workshoppen vil du ha:

* Lært om DDD og fordelene med en slik tankegang og arkitektur.
* Oppdaget hvordan funksjonell programmering passer godt sammen med DDDs workflow-prinsipper.
* Fått prøve å kode på en måte som er både funksjonell og domene-drevet.

## 💡 Introduksjon
Vi har fått et spennende oppdrag fra ingen ringere enn Magnus Midtbø. Magnus har nemlig besluttet å åpne en nettbutikk som skal selge klatreutstyr og T-skjorter – alt designet for å inspirere til å klatre høyere og sikrere!

Etter å ha hørt noe snakk om Domene dreven design og viktigigheten av et produktteam, samler du sammen Magnus og gjengen og kjører en Event Storming.
Der kommer dere fram til en avgrenset kontekst (Bounded Context) som Magnus gjerne vil at du tar ansvar for. 
Spesifikt er dette Bestillingskonteksten som har ansvar for å ta imot en  bestilling og behandle denne. Dette blir dere enige om å kalle `PlasserBestillingWorkflow`. Magnus forteller at den består av følgende steg:

1. Ikke validerte bestillinger blir mottatt via Youtube-kommentarer på videoene til Magnus. Problemet er at kommentarene ofte inneholder mangelende eller feil data. Alle bestillinger må derfor nøye valideres.
2. Deretter skal bestillinges prises. Alle ordrelinjer skal prises og totalen skal beregnes. Selve prisingen ligger i et egen system (en annen Bounded Context), og vi kan bruke en ekstern tjeneste for å hente dette.-
3. Deretter skal bestillingen bekreftes per epost til brukeren, bestillingen skal sendes til frakavdelingen og fakturaavdelingen skal motta prisinformasjon.

"Topp!", - tenker du. Dette kan jeg jo modelere som en kontiunerlig workflow uten sideeffekter!  

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

## 📚 Ressurser
Workshoppen og oppgaven er inspirert av boken [Domain Modelling Made Functional](https://github.com/swlaschin/DomainModelingMadeFunctional). Deler av oppgaven er portet fra F# til Kotlin.

Deler av kodebase bruker Result-typen fra `kotlin-result`. Dokumentasjonen finnes [her](https://github.com/michaelbull/kotlin-result).

## 👩‍💻 Oppgaver

### Kom i gang
For å komme i gang med oppgaven, følg disse trinnene:

1. Klon ned kodebasen fra GitHub ved å kjøre følgende kommando i terminalen:
```bash
git clone https://github.com/Quist/funksjonell-ddd.git
```

2. Åpne prosjektet i IntelliJ
IntelliJ IDEA er en anbefalt editor for å jobbe med Kotlin og Gradle-prosjekter.
Last ned IntelliJ IDEA hvis du ikke allerede har det installert.
Åpne IntelliJ, og importer prosjektet som et eksisterende Gradle-prosjekt.

3. Du finner oppgavene lenger ned her. Lykke til!

## 📋 Oppgaver 
Det er skrevet JUnit tester for flere av oppgavene. Det kan være et lurt sted å starte for å få oversikt over oppgaven og validere løsningen. Se `Oppgaver.kt`

### Del 1 - Validering
> I DDD handler mye av designet om å beskytte domenet og sørge for at det forretningslogiske laget forblir konsistent og robust. Validering av input hjelper med å forhindre at ugyldige eller uventede data når inn til kjerneobjektene og ødelegger forretningslogikken.

#### Oppgave 1a
Magnus spør om du vil være med å klatre. I det du sikrer Magnus og han er på vei opp i veggen, forteller han at han har fått en noen sinte eposter fra fraktavdelingen. De mottar masse bestillinger med ugyldige gateadresser.

* Endre `ValidertAdresse`-typen slik at konstruktøren blir privat. Legg til en companion object med en create-metode som sørger for at gateadressefeltet ikke et tomt.

#### Oppgave 1b
I det Magnus klipper seg inn i første klipp, tar han opp et problem rundt postnummer. For Magnus og alle andre i firmaet er det ganske "selvsagt" at norske postnummer er tallverdier mellom 0001 og 9999. 

"Ahh",- tenker du inne i deg. Hvis det er sånn de snakker om det, så bør vi nok også modelere det sånn.

* Innfør en ny type, `Postnummer`. Endre feltet postnummer i `ValidertAdresse` til å være av denne typen. `Postnummer` skal ha som invariant at postnummer er et tall mellom 0001 og 9999. Du kan kaste en `ugyldigAdresse`-exception om det ikke er det.

#### Oppgave 1c 
Magnus forsetter å prate med han klatrer oppover. Han forteller om at selv om det formelt sett er gyldige adresser som sendes inn, så hender det at adressen rett og slett ikke finnes!
Du feilsøker litt og ser fort at TeamMedlem fra et konkurrende konsulentselskap har lagt igjen en TODO i `tilValidertAdresse`funksjonen.

* Implementer en sjekk av at adressen faktisk finnes i `tilValidertAdresse`

> Vi lar være her, men her kunne vi også valgt å innføre en ny type for å både reflektere en gyldig og eksisterende adresse:
> ```data class ValidertOgEksisterendeNorskAdresse ..```

#### Oppgave 2
TODO: Kundeinfo og validert epost

#### Oppgave 3
TODO: Noe med ordrelinjer
> Hint: Sjekk ut NonEmptyList typen som ligger under `utils/`
> 

### Del 2 Priset bestilling

### Del 3 Events

#### 💰Del 4 -Videreutvikle designet 
I denne delen av workshoppen jobber vi videre med endringer i kravene fra Magnus.
Målet er å reflektere over hvordan endringer påvirker både domenemodellen og koden, og å se hvordan en domene-drevet tilnærming kan håndtere slike justeringer.

Det finnes ingen fasit her – det er opp til deg hvordan du velger å løse oppgavene, og poenget er å lære gjennom å eksperimentere og reflektere.Vi ser på 4 forskjellige typer endringer:
* Legge til et nytt steg i workflowen.
* Legge til input i workflowen.
* Endre definisjonen av en kjernetype og se hvordan det påvirker systemet.
* Endre hele arbeidsflyten til å stemme med forretningregler. 

#### Oppgave X_1 Legge til fraktkostnader
En sen søndagskveld legger du i vei til et lokalt klatresenter i Oslo. Når du kommer kommer inn får du øye på en kar i bar overkropp som filmer seg selv. Akkurat i det du innser at det er Magnus, så roper han på deg:
Det viser seg at det er store problemer med inntjeningen på nettsalget. Magnus sier at han helt har glemt å tenke på at det koster penger for frakt!

a: Legg til et steg for å legge til fraktkostader, f.eks etter prisOrdre-steget.
b: Inkluderer fraktkostnaden i bekreftelseseposten.

#### Oppgave X_2 Legge til støtte for VIP-kunder

#### Oppgave X_3 Legge til støtte for promokoder

#### Oppgave X_4 Implementere søndagsstengt

### 🌟 Bonusoppgaver

#### Bruk Result-typen istedenfor å kaste exceptions
Refaktorere kodebasen til å bruke Result-typen i stedet for å kaste exceptions. 

> **Hvorfor bruke result-typen?**
> 
> 
> Funksjonell feilhåndtering: Result gjør det tydelig hvordan feil skal håndteres, og tvinger kallere til å forholde seg til mulige feil, noe som reduserer risikoen for uventede programfeil.
>
> Bedre lesbarhet: Koden blir mer lesbar og forståelig ved at feilhåndtering er en del av metodesignaturen, og det er lett å se hvilke situasjoner som kan føre til feil.
> 
> Ingen skjulte kontrollflytendringer: Exceptions kan gjøre kontrollflyten uforutsigbar, mens Result gjør flyten eksplisitt og enklere å følge.