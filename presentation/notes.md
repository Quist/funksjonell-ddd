# Hvorfor domene dreven design? 🧐

Det handler om _smidig_. Her kan vi dra noen paralleller til at domene dreven design egentlig følger mange smidige prinsipper.

Vår jobb som utvikler er ikke nødvendigvis å shippe kode til prod, men heller å levere en form for effekt/verdi gjennom å lage IT-løsninger. Koding er således bare en del av det.

## Viktigheten av en delt mental model 🧑‍🎓🧑‍💻🧑‍🎨👩‍🔧
Å faktisk forstå problemene teamet skal løse er avgjørende for et team. Ofte kan det være mange ledd mellom utviklerene som løser problemet og brukerne som faktisk bruker systemet.

*** Definere mentall modell. *** 

Ulike stadier for et team.
* Lvl 1: Krav kommer inn til teamet, og teamet løser det fossefall-style.
* Lvl 2:  Smidig, man trekker fagpersoner inn i teamet og itererer hyppig sammen.
* Lvl 3: Selve DDD-nirvana. Domeneekspertenbe, utviklerene, stakeholder og kildekoden deler samme modell. Det trengers ikke oversettes fra krav til kode. Koden er designet til å reflektere den delte mentale modellen direkte. 


Fordeler når vi har en delt mental modell:
* Kjappere tid til markedet.
* Mer verdi levert.
* Mindre sløsing av tid på ting som egentlig ikke betyr noe.
* Lettere vedlikehold og forvaltning.

### Hvordan skape en delt mental modell
Miljøet har definert noen tips:
* Fokus på foretningseventer og arbeidsflyter istedenfor datastrukturer. 
* Del opp problemområde inn i mindre områder.
* Lag en modell for hvert subproblemområde.
* Utvikl et felles språk som dels blant alle i prosjektet og også blir brukt i koden. 

#### Forstå domenet gjennom foretningsprosesser
En virksomhet får ingen verdi av data alene, det er gjerne hvordan dataen transformeres som skaper en eller annen verdi. Vi kan derfor se på foretningsprosesser som en serie av datatransformeringer.
Siden verdien skapes her er det kritisk å se hvordan disse transformeringene gjøres og hvordan de relaterer til hverandre.

Men hva er det som trigger eventene? Dette kaller vi for ***Domain Events***. Domain events er utgangspunktet for nesten alle foretningsprosessene vi modelererer. Eksempel: "new order form received"

##### Event Storming
Placeholder


##### Begrep
Scenario: Beskriver et mål som en bruker ønsker å få gjennomført, f.eks å legge inn en ordre. Use case er en mer detaljert versjon av scenario.

Business Process: Beskriver et mål som forettningen (ikke brukeren) ønsker å oppnå. Likner på scenario, men tar utgangspunkt i foretningen.

Workflow: Detaljert beskrivelsen av en del av foretningsprossess. Lister de eksakte stegene som trengs for å oppnå et foretningsmål eller subgoal.

Command: A command that triggers and vent


## Notater til outline for presentasjon

* Agenda
* Presentere DDD
  * Historie
  * Relasjon til smidig
  * Status i dag
* Dypdykk
  * Delt Modell 
* Hvorfor DDD passer godt med funksjonell tankegang



Andre notater:
* Kanskje det finnes noen fine videoer?
* Kanskje ta med noe om din motivasjon for dette.
* Kanskje huske å selge det godt inn som en enabler for smidig og midt i blinken for produktutvikling?
* Kanskje ta utgangspunkt i presentasjonen om hvordan man ville gått fram DDD-style for å utvikle et nytt prosjekt? Også introdusere konseptene på den måten.
* Målet med domene-dreven-design: Kode som reflekterer den delte mentale modellen direkte.