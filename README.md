# 👓 Domene-drevet Design - med funksjonelle briller
Workshop for faggruppen *Arkitektur i Praksis*. Etter å ha deltatt i denne workshoppen vil du ha:

* Lært om domene-drevet design (DDD) og fordelene med en slik tankegang og arkitektur.
* Oppdaget hvordan funksjonell programmering passer godt sammen med DDD.
* Kodet både funksjonelt og domene-drevet i Kotlin.

## 💡 Introduksjon
Vi har fått et spennende oppdrag fra ingen ringere enn Magnus Midtbø. Magnus og vennene hans har nemlig besluttet å åpne en nettbutikk som skal selge klatreutstyr.
Han slenger rundt seg med uttrykk og fraser du aldri har hørt før: han skal selge Black Diamond "nøtter" og "kammer", kalk, cordeletter og du vet ikke hva.
Ettersom du nylig har hørt om **domene-dreven design** og viktigheten av tverrfaglige produktteam, samler du sammen Magnus og gjengen og kjører en **Event Storming**.

Dere kommer fram til en avgrenset kontekst (Bounded Context), **Bestillingskonteksten**, som Magnus gjerne vil at du tar ansvar for. 
Dere blir enige om å kalle `PlasserBestillingWorkflow`. Magnus forteller at den består av følgende steg:

1. **Ikke validerte bestillinger** blir mottatt via Youtube-kommentarer på videoene til Magnus. Problemet er at kommentarene ofte inneholder manglende eller feil data. Alle bestillinger må derfor nøye **valideres**.
2. Deretter skal bestillinges **prises**. Alle ordrelinjer skal prises og totalen skal beregnes. Selve prisingen ligger i et eget system utenfor vår kontekst, og vi kan bruke en ekstern tjeneste for å hente dette.
3. Deretter skal bestillingen **bekreftes** per e-post til brukeren, bestillingen skal sendes til **fraktavdelingen** og **fakturaavdelingen** skal informasjon om hva som skal prises.

"Topp!", - tenker du. Dette kan jeg jo modelere som en kontinuerlig workflow uten sideeffekter!  

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
    Sideeffekter["`Hendelser:
    - Bestilling bekreftet til kunde
    - Bestilling til fraktavdelingen
    - Bestilling til fakturaavdeling`"]
    OnSuccess["`Suksess:
    - Bekreftelse sendt
    - Bestilling plassert
    - Bestilling sendt fakturaavdeling`"]
    
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


## 📋 Kom i gang
1. Klon ned kodebasen fra GitHub ved å kjøre følgende kommando i terminalen:
```bash
git clone https://github.com/Quist/funksjonell-ddd.git
```

2. Åpne prosjektet i IntelliJ.

IntelliJ IDEA er en anbefalt editor for å jobbe med Kotlin og Gradle-prosjekter.
Last ned IntelliJ IDEA hvis du ikke allerede har det installert.
Åpne IntelliJ, og importer prosjektet som et eksisterende Gradle-prosjekt.

3. Du finner oppgavene lenger ned her. Lykke til!

## 👩‍💻 Oppgaver
Det er skrevet JUnit tester for flere av oppgavene. Det kan være et lurt sted å starte for å få oversikt over oppgaven og validere løsningen. Se `Oppgaver.kt`. Implementasjonskoden er i `BestillingImpl.kt`

> [!NOTE]
> I DDD er en viktig del av designet å beskytte domenet og sørge for at det forretningslogiske laget forblir konsistent og robust. Validering av input hjelper med å forhindre at ugyldige eller uventede data når inn til kjerneobjektene og ødelegger forretningslogikken.

### Oppgave 1a
Magnus spør om du vil være ut å klatre. I det du sikrer Magnus og han er på vei opp i veggen, forteller han at han har fått noen sinte e-poster fra fraktavdelingen. De mottar masse bestillinger med ugyldige gateadresser.

* **Endre `ValidertAdresse`-typen slik at konstruktøren blir privat. Legg til en companion object med en create-metode som sørger for at gateadressefeltet ikke et tomt.**
* **Husk å få testen til å passere!**

### Oppgave 1b
I det Magnus klipper seg inn i første klipp, tar han opp et problem rundt postnummer. For Magnus og de andre er det ganske "selvsagt" at norske postnummer er tallverdier mellom 0001 og 9999. 

_"Ahh",_- tenker du inne i deg. Hvis det er sånn de snakker om det, så bør vi nok også modelere det sånn.

* **Innfør en ny type, `Postnummer`. Endre feltet postnummer i `ValidertAdresse` til å være av denne typen. `Postnummer` skal ha som invariant at postnummer er et tall mellom 0001 og 9999. Du kan kaste en `ugyldigAdresse`-exception om det ikke er det.**

### Oppgave 1c 
Magnus forsetter å prate mens han klatrer oppover. Han forteller om at selv om det formelt sett er gyldige adresser som sendes inn, så hender det at adressen rett og slett ikke finnes!
Du feilsøker litt og ser fort at et teammedlemm fra et konkurrende konsulentselskap har lagt igjen en TODO i `tilValidertAdresse`funksjonen.

* **Implementer en sjekk av at adressen faktisk finnes i `tilValidertAdresse`.**

> [!NOTE]
> Vi kan la det være her, men her kunne vi også valgt å innføre en ny type for å både reflektere en gyldig og eksisterende adresse:
> ```data class ValidertOgEksisterendeNorskAdresse ..```

### Oppgave 2a
Etter å ha toppet ut ruta, firer Magnus seg ned mot bakken. På vei ned forteller han om et annet problem de har hatt: ugyldige e-poster som blir sendt inn i systemet.

* **Implementer en validering av e-post som validerer at det ihvertfall er en alfakrøll i e-posten.**

> [!TIP]
> Du kan løse dette på flere måter. Ett eksempel er ved å innføre en egen e-post data class.

### Oppgave 2b
I det Magnus setter foten på bakken igjen, hører du noen voldsomme skrik fra en langbeint klatrer i naboveggen. Magnus titter raskt bort, før han forteller videre om hvordan de ser for seg å modellere e-post.
Ikke bare er den validert – en e-post skal verifiseres at den faktisk tilhører brukeren. Magnus foreslår derfor at dere endrer den delte mentale modellen for e-post til å være en slags union type alá `Verifiserte-post | Uverifiserte-post`

```kotlin
sealed interface EpostStatus {
    val ePpost: EpostStatus
}
data class ValidertBestilling(..., val ePost: EpostStatus)
```

1. **Implementer to dataklasser, `VerifisertEpost` og `UverifisertEpost`, som arver fra Sealed Interface EpostStatus.**
2. **Implementer de nødvendige kodeendringene i implementasjonen etter at domenetypene er oppdatert.**

> [!TIP]
> Sjekk ut dokumentasjonen for Sealed Interface om du trenger hjelp 🧠
> 
> Det finnes en dependency Sjekke-postStatus som kan brukes. Husk at den bør sendes med i hovedfunksjonen.

### Oppgave 3a
Den høylytte klatreren kommer bort. Det viser seg at han heter Adam Ondra og er endel av virksomheten til Magnus. Adam forteller om enda et problem. Det kommer mange bestillinger inn i systemet som ikke har noen ordrelinjer! Da blir det bare støy for faktura- og regnskapsavdelingen.

**Implementer en endring i domenetypene slik at listen aldri kan være tom.**
> [!TIP]
> Hint: Sjekk ut NonEmptyList typen som ligger under `utils/`.
> 
> Konstruktøren i NonEmptyList returnerer et Result. Sjekk resultatet. Du kan kaste en `UgyldigeOrdreLinjer`-exception om listen var tom.

### Del 2 Pris Bestilling
TODO Oppgaver relatert til å prise bestillingen. Tema? Kanskje noe med agregat. Eller noen enklere oppgaver bare for å bli kjent med steget.

### Del 3 Bekreft bestilling steg
TODO Oppgave med å legge til e-post i bekreftelese-post

### Del 4 Outputten av funksjonen – Events med sideeffekter
TODO Oppgaver. Kanskje noe med å skrive/fylle ut tester på den faktiske outputten av funksjonen.

### 💰Del 5 -Videreutvikle designet 
I denne delen av workshoppen jobber vi videre med endringer i kravene fra Magnus.
Målet er å reflektere over hvordan endringer påvirker både domenemodellen og koden, og å se hvordan en domene-drevet tilnærming kan håndtere slike justeringer.

Det finnes ingen fasit her – det er opp til deg hvordan du velger å løse oppgavene, og poenget er å lære gjennom å eksperimentere og reflektere.Vi ser på 4 forskjellige typer endringer:

#### Oppgave Legge til fraktkostnader
Det viser seg at det er store problemer med inntjeningen på nettsalget. Magnus sier at han helt har glemt å tenke på at det koster penger for frakt!

* **Legg til et steg for å legge til fraktkostader, f.eks etter prisOrdre-steget.**
* **Inkluderer fraktkostnaden i bekreftelsese-posten.**

> [!Note] 
> Å definere det som et selvstendig steg kan være nyttig for å tydeligjøre hva som skjer i foretningsprosessen. 

#### Oppgave Legge til støtte for VIP-kunder
Magnus vil gjerne at alle klatrevenne hans skal få gratisk frakt. Han vil gjerne kalle det VIP-kunder, og tenker at det kan komme flere fordeler etterhvert.

* Endre på `KundeInfo` i `ValidertBestilling` til å representere VIP-kunder
* Legg til et steg, eller endre et steg, for å implementere dette.


#### Oppgave Legge til støtte for promokoder
Etter en diskusjon med salgsteamet til Magnus kommer dere opp med følgende krav:

* Når man legger inn en bestilling, _kan_ kunden sende inn en promokode.
* Hvis koden er tilstede, så vil noen produkter gi andre priser.
* Bestillingen bør vise at en promokode var brukt.

> [!TIP]
> Tilsynelatede uskylding, vil den siste kravet påvirke store deler av domenet vårt. Lykke til!

#### Oppgave Implementere søndagsstengt
Magnus jobber ikke søndager når det er godt klatrevær.

**Implementer at systemet er helt søndagssteng**

> [!Tips]
> Adapter function

### 🌟 Bonusoppgaver

#### Bruk Result-typen istedenfor å kaste exceptions
Refaktorere kodebasen til å bruke Result-typen i stedet for å kaste exceptions. 

> [!TIP]
> **Hvorfor bruke result-typen?**
> 
> 
> Funksjonell feilhåndtering: Result gjør det tydelig hvordan feil skal håndteres, og tvinger kallere til å forholde seg til mulige feil, noe som reduserer risikoen for uventede programfeil.
>
> Bedre lesbarhet: Koden blir mer lesbar og forståelig ved at feilhåndtering er en del av metodesignaturen, og det er lett å se hvilke situasjoner som kan føre til feil.
> 
> Ingen skjulte kontrollflytendringer: Exceptions kan gjøre kontrollflyten uforutsigbar, mens Result gjør flyten eksplisitt og enklere å følge.