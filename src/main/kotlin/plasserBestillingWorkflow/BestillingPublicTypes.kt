package plasserBestillingWorkflow

import com.github.michaelbull.result.Result
import java.lang.RuntimeException
import java.time.LocalDateTime


// ==================================
// Denne filen inneholder definisjonene av offentlige typer (eksponert ved grensen av denne Bounded Context) relatert til PlasserBestilling
// ==================================

// Hovedworkflow
typealias PlasserBestillingWorkflow = (Bestilling) -> Result<List<PlasserBestillingHendelse>, Valideringsfeil>
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
    data class IkkeValidertOrdrelinje(val produktkode: String, val mengde: String)
    data class IkkeValidertAdresse(val gateadresse: String?, val postnummer: String?)
}

// Output
sealed class PlasserBestillingHendelse {
    class BekreftelseSentTilBrukerHendelse() : PlasserBestillingHendelse()
    data class BestillingAkseptertHendelse(val ordre: PrisetBestilling) : PlasserBestillingHendelse() // For fraktavdelingen
    data class FakturaHendelse(val ordreId: OrdreId, val fakturadresse: ValidertAdresse, val fakturasum: Pris) : PlasserBestillingHendelse() // For fakturaavdelingen
}

// Ting som kan gå galt
class UgyldigOrdreException(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigAdresse(validationMessage: String) : RuntimeException(validationMessage)
class UkjentAdresse(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigEpost(validationMessage: String) : RuntimeException(validationMessage)
class EpostIkkeVerifisert(validationMessage: String) : RuntimeException(validationMessage)
class UgyldigeOrdreLinjer(validationMessage: String) : RuntimeException(validationMessage)
class FantIkkeProduktkode(validationMessage: String) : RuntimeException(validationMessage)
typealias Valideringsfeil = String

// Dependencies
typealias SjekkProduktKodeEksisterer = (String) -> Boolean
typealias SjekkAdresseEksisterer = (IkkeValidertBestilling.IkkeValidertAdresse) -> Boolean
typealias HentProduktPris = (produktkode: Produktkode) -> Double
typealias LagBekreftelsesEpostHtml = (PrisetBestilling) -> HtmlString
typealias SendBekreftelsesEpost = (email: String, letter: HtmlString) -> SendEpostResultat
typealias SjekkEpostVerifiseringsStatus = (email: String) -> Boolean

// Typer for dependencies
enum class SendEpostResultat {
    Sendt,
    Ikke_sendt
}
typealias HtmlString = String