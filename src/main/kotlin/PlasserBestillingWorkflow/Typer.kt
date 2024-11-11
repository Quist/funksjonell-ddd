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
value class Produktkode(val value: String)

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
value class Mengde private constructor(val value: Int) {
    companion object {
        fun of(value: Int): Mengde {
            if (value <= 0) {
                throw IllegalStateException("Kilogrammengde må være positiv")
            }
            return Mengde(value)
        }
    }
}

data class PrisetBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaadresse: ValidertAdresse,
    val fakturaSum: Pris
)