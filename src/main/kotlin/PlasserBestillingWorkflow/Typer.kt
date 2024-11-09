package PlasserBestillingWorkflow

@JvmInline
value class OrdreId private constructor(val value: String) {
    companion object {
        fun of(value: String): OrdreId {
            if (value.isEmpty()) {
                throw UgyldigOrdreException("OrdreId må ha innhold")
            }
            return OrdreId(value)
        }
    }
}

@JvmInline
value class KundeId private constructor(val value: String) {
    companion object {
        fun of(value: String): KundeId {
            if (value.isEmpty()) {
                throw UgyldigOrdreException("KundeID må våre 8 tegn langt")
            }
            return KundeId(value)
        }
    }
}

// Produktkode
@JvmInline
value class KlatreutstyrKode(val value: String)

@JvmInline
value class TskjorteKode(val value: String)

sealed class Produktkode {
    data class Klatreutstyr(val kode: KlatreutstyrKode) : Produktkode()
    data class Tskjorte(val kode: TskjorteKode) : Produktkode()
}

@JvmInline
value class Pris private constructor(val value: Number) {
    companion object {
        fun of(value: Number): Pris {
            if (value.toDouble() <= 0) {
                throw UgyldigOrdreException("Pris må være større en 0")
            }
            return Pris(value)
        }
    }
}

// Ordremengde typer

@JvmInline
value class Kilogrammengde private constructor(val value: Float) {
    companion object {
        fun of(value: Float): Kilogrammengde {
            if (value <= 0) {
                throw IllegalStateException("Kilogrammengde må være positiv")
            }
            return Kilogrammengde(value)
        }
    }
}

// TODO Oppgave 2b: Implmenter logikk for å hindre at enhetsmengde kan være et ugyldig tall
@JvmInline
value class Enhetsmengde(val value: Number)

sealed class OrdreMengde {
    data class Enhet(val mengde: Enhetsmengde) : OrdreMengde()
    data class Kilo(val mengde: Kilogrammengde) : OrdreMengde()
}

data class PrisetBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaadresse: ValidertAdresse,
    val faktureringssum: Pris
)