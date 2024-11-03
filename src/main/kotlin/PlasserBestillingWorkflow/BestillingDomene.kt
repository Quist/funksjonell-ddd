package PlasserBestillingWorkflow

import com.github.michaelbull.result.Result
import utils.NonEmptyList
import java.lang.RuntimeException
import java.time.LocalDateTime

// Representerer vår først workflow. En funksjon som tar en uvalidert bestilling og konverterer den til hendelser.
typealias PlasserBestillingWorkflow = (Bestilling) -> Result<BestillingPlassertHendelser, Valideringsfeil>

// Sub-workflows
typealias ValiderBestilling = (SjekkProduktKodeEksisterer, SjekkAdresseEksisterer, IkkeValidertBestilling) -> Result<ValidertBestilling, Valideringsfeil>
typealias PrisOrdre = (HentProduktPris) -> (ValidertBestilling) -> PrisetBestilling

// Hjelpefunksjoner (typisk services i objekt-orienterte språk)
typealias SjekkProduktKodeEksisterer = (String) -> Boolean
typealias SjekkAdresseEksisterer = (String) -> Boolean
typealias HentProduktPris = () -> Nothing


data class Bestilling(val bestilling: IkkeValidertBestilling, val time: LocalDateTime, val userId: String)

// Produktkode
@JvmInline
value class KlatreutstyrKode(val value: String)

@JvmInline
value class TskjorteKode(val value: String)

sealed class Produktkode {
    data class Klatreutstyr(val kode: KlatreutstyrKode) : Produktkode()
    data class Tskjorte(val kode: TskjorteKode) : Produktkode()
}

// Ordremengde typer


@JvmInline
value class Kilogrammengde private constructor(val value: Float) {
    companion object {
        fun of(value: Float): Kilogrammengde {
            if (value <=0) {
                throw IllegalStateException("Kilogrammengde må være positiv")
            }
            return Kilogrammengde(value)
        }
    }
}

// TODO Oppgave 2b: Implmenter logikk for å hindre at enhetsmengde kan være et ugyldig tall
@JvmInline
value class Enhetsmengde(val value: Int)

sealed class OrdreMengde {
    data class Enhet(val mengde: Enhetsmengde) : OrdreMengde()
    data class Kilo(val mengde: Kilogrammengde) : OrdreMengde()
}


// Modelere en bestilling
data class ValidertBestilling(
    private val id: OrdreId,
    private val kundeId: KundeId,
    private val leveringsadresse: ValidertAdresse,
    private val fakturaAdresse: FakturaAdresse,
    private val ordrelinjer: NonEmptyList<Ordrelinje>, // Endre tilbake til vanlig list for å få testen til å feile.
    private val sumSomSkalBliBelastet: FakturaSum
)

data class ValidertAdresse(val adresselinje: String)

data class PrisetBestilling(val tmp: String)

data class Ordrelinje(
    private val id: OrdrelinjeId,
    private val ordreId: OrdreId,
    private val produktkode: Produktkode,
    private val ordreMengde: OrdreMengde,
    private val pris: Pris
)

typealias OrdreId = String
typealias KundeId = Placeholder
typealias FakturaAdresse = Placeholder
typealias OrdrelinjeId = Placeholder
typealias FakturaSum = Placeholder
typealias Pris = Placeholder
typealias Placeholder = String

// Representerer en bestilling som har kommet inn i systemet. Ingenting er validert enda, så kun primitive typer.
data class IkkeValidertBestilling(
    val ordreId: String,
    val kundeinfo: String,
    val leveringsadresse: String,
    val ordrelinjer: List<IkkeValidertOrdrelinje>
) {
    data class IkkeValidertOrdrelinje(val produktkode: String, val mengde: Number)
}

// Representerer outputten av PlasserBestilling - ulike hendelser
data class BestillingPlassertHendelser(
    val bekreftelseSent: Boolean,
    val ordrePlassert: Boolean,
    val fakturerbarOrdrePlassert: Boolean
)
typealias Valideringsfeil = String

// Exceptions
public class UgyldigOrdreException(validationMessage: String) : RuntimeException(validationMessage)