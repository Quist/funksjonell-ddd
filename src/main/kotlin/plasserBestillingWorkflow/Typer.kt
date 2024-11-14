package plasserBestillingWorkflow

// Steg 1: Valider Bestilling
data class ValidertBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaAdresse: ValidertAdresse,
    val ordrelinjer: List<ValidertOrdrelinje>,
)

data class ValidertOrdrelinje(
    val produktkode: Produktkode,
    val ordreMengde: Mengde,
)

data class ValidertAdresse(val gateadresse: String, val postnummer: Number)
data class KundeInfo(val kundeId: KundeId, val kundeEpost: String)
typealias ValidertEpost = Nothing // Placeholder for oppgave 2a
typealias Epost = Nothing // Placeholder for oppgave 2b

// Steg 2: Pris bestilling
data class PrisetBestilling(
    val ordreId: OrdreId,
    val kundeInfo: KundeInfo,
    val leveringsadresse: ValidertAdresse,
    val fakturaadresse: ValidertAdresse,
    val fakturaSum: Pris,
    val priseteOrdrelinjer: List<PrisetOrdrelinje>
)
data class PrisetOrdrelinje(val linjePris: Pris)

// Steg 3 - Bekreft Bestilling
data class BekreftetBestilling(val sendEpostResultat: SendEpostResultat, val prisetBestilling: PrisetBestilling)


// Hjelpetyper

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

@JvmInline
value class Produktkode(val value: String)

@JvmInline
value class Pris private constructor(val value: Number) {
    companion object {
        fun of(value: Number): Pris {
            if (value.toDouble() < 0) {
                throw UgyldigOrdreException("Pris må være større en 0")
            }
            return Pris(value)
        }
    }
}

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
