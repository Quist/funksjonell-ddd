package PlasserBestillingWorkflow

import com.github.michaelbull.result.Result
import java.lang.RuntimeException
import java.time.LocalDateTime


// ==================================
// Denne filen inneholder definisjonene av offentlige typer (eksponert ved grensen av denne Bounded Context) relatert til PlasserBestilling
// ==================================

// Hovedworkflow
typealias PlasserBestillingWorkflow = (Bestilling) -> Result<PlasserBestillingHendelser, Valideringsfeil>
// Input
data class Bestilling(val bestilling: IkkeValidertBestilling, val time: LocalDateTime)
data class IkkeValidertBestilling(
    val ordreId: String,
    val kundeId: String,
    val kundeEpost: String,
    val leveringsadresse: IkkeValidertAdresse,
    val fakturadresse: IkkeValidertAdresse,
    val ordrelinjer: List<IkkeValidertOrdrelinje>
) {
    data class IkkeValidertOrdrelinje(val produktkode: String, val mengde: Number)
    data class IkkeValidertAdresse(val gateadresse: String?, val postnummer: String?)
}

// Output
data class PlasserBestillingHendelser(
    val bekreftelseSent: SendEpostResultat,
    val ordrePlassert: PrisetBestilling, // Blir sendt til fraktavdelingen
    val fakturerbarOrdrePlassert: FakturerbarOrdrePlassert? // Blir sendt til fakturaavdelingen kun hvis det sum >
)
data class FakturerbarOrdrePlassert(val ordreId: OrdreId, val fakturadresse: ValidertAdresse, val fakturasum: Pris)

// Ting som kan gå galt
class UgyldigOrdreException(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigAdresse(validationMessage: String) : RuntimeException(validationMessage)
class UkjentAdresse(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigEpost(validationMessage: String) : RuntimeException(validationMessage)
class EpostIkkeVerifisert(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigeOrdreLinjer(validationMessage: String) : RuntimeException(validationMessage)
typealias Valideringsfeil = String

// Dependencies
typealias SjekkProduktKodeEksisterer = (String) -> Boolean
typealias SjekkAdresseEksisterer = (IkkeValidertBestilling.IkkeValidertAdresse) -> Boolean
typealias HentProduktPris = (produktkode: Produktkode) -> Int
typealias LagBekreftelsesEpostHtml = (PrisetBestilling) -> HtmlString
typealias SendBekreftelsesEpost = (email: String, letter: HtmlString) -> SendEpostResultat
typealias SjekkEpostValideringsStatus = (email: String) -> Boolean

// Typer for dependencies
enum class SendEpostResultat {
    Sendt,
    Ikke_sendt
}
typealias HtmlString = String