package PlasserBestillingWorkflow

import com.github.michaelbull.result.Result
import java.lang.RuntimeException
import java.time.LocalDateTime


// ==================================
// Denne filen inneholder definisjonene av OFFENTLIGE typer (eksponert ved grensen av denne Bounded Context)
// relatert til PlasserBestilling
// ==================================

// ------------------------------------


// Hovedworkflow
typealias PlasserBestillingWorkflow = (Bestilling) -> Result<BestillingPlassertHendelser, Valideringsfeil>

// Input
data class Bestilling(val bestilling: IkkeValidertBestilling, val time: LocalDateTime, val userId: String)
data class IkkeValidertBestilling(
    val ordreId: String,
    val kundeinfo: String,
    val leveringsadresse: String,
    val ordrelinjer: List<IkkeValidertOrdrelinje>
) {
    data class IkkeValidertOrdrelinje(val produktkode: String, val mengde: Number)
}

// Output
data class BestillingPlassertHendelser(
    val bekreftelseSent: Boolean,
    val ordrePlassert: Boolean,
    val fakturerbarOrdrePlassert: Boolean
)

// Ting som kan gå galt
class UgyldigOrdreException(validationMessage: String) : RuntimeException(validationMessage)
typealias Valideringsfeil = String


// Todo: Vurdere å flytte
data class PrisetBestilling(val tmp: String)
typealias KundeId = Placeholder
typealias FakturaAdresse = Placeholder
typealias FakturaSum = Placeholder
typealias Pris = Placeholder
typealias Placeholder = String
