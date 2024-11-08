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
data class Bestilling(val bestilling: IkkeValidertBestilling, val time: LocalDateTime)
data class IkkeValidertBestilling(
    val ordreId: String,
    val kundeinfo: String,
    val leveringsadresse: String,
    val fakturadresse: String,
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
class UgyldigAdresse(validationMessage: String) : RuntimeException(validationMessage)
typealias Valideringsfeil = String


// Todo: Vurdere å flytte
typealias FakturaSum = Placeholder
typealias Pris = Placeholder
typealias Placeholder = String
