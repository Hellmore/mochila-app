package br.com.mochila.util

object TempEmailValidator {

    private val disposableDomains = setOf(
        // Mailinator family
        "mailinator.com", "mailinater.com", "mailinator2.com", "mailinator.us",
        // Guerrilla Mail family
        "guerrillamail.com", "guerrillamail.net", "guerrillamail.org",
        "guerrillamail.biz", "guerrillamail.de", "guerrillamail.info",
        "guerrillamailblock.com", "grr.la", "spam4.me",
        "sharklasers.com", "jourrapide.com", "armyspy.com",
        // 10 Minute Mail
        "10minutemail.com", "10minutemail.net", "10minutemail.org",
        "10minutemail.de", "10minutemail.co.uk", "10minemail.com",
        "10mail.org",
        // Yopmail family
        "yopmail.com", "yopmail.fr", "yopmail.net", "yopmail.gq",
        "yop.email", "cool.fr.nf", "jetable.fr.nf", "nospam.ze.tc",
        "nomail.xl.cx", "mega.zik.dj", "speed.1s.fr", "courriel.fr.nf",
        "moncourrier.fr.nf", "monemail.fr.nf", "monmail.fr.nf",
        // Trash Mail family
        "trashmail.com", "trashmail.at", "trashmail.io", "trashmail.me",
        "trashmail.net", "trashmail.xyz", "trashmail.org",
        "trash-mail.at", "trash-mail.cf", "trash-mail.ga",
        "trash-mail.gq", "trash-mail.ml", "trash-mail.tk",
        "trashdevil.com", "trashdevil.de", "trashinbox.com",
        // Temp Mail
        "tempmail.com", "tempmail.net", "tempmail.org", "tempmail.eu",
        "temp-mail.org", "temp-mail.io", "temp-mail.ru",
        "tempemail.com", "tempr.email", "tempalias.com",
        "temporaryemail.net", "temporaryemail.us", "temporaryforwarding.com",
        "temporarymailaddress.com", "tempinbox.com", "tempinbox.co.uk",
        "tempomail.fr", "tempmailer.com", "mytempemail.com",
        "timed.email", "one-time.email",
        // Discard / throwaway
        "discard.email", "discardmail.com", "discardmail.de",
        "throwaway.email", "throwam.com", "throwam.net",
        "throwsmoke.com", "dontreg.com",
        // GetNada / Nada
        "getnada.com", "nada.email", "nadaemail.com",
        // Maildrop
        "maildrop.cc",
        // Mailnull
        "mailnull.com",
        // Dispostable
        "dispostable.com",
        // Spamgourmet
        "spamgourmet.com", "spamgourmet.net", "spamgourmet.org",
        // Spam* family
        "spam.la", "spam.su", "spamex.com", "spamfree.eu",
        "spamfree24.org", "spamgap.com", "spamherelots.com",
        "spamspot.com", "spamthisplease.com", "spamtrail.com",
        // Fakeinbox
        "fakeinbox.com", "fakemail.net", "fakemailgenerator.com",
        "fake-email.net",
        // Emailondeck
        "emailondeck.com",
        // Wegwerf
        "wegwerfmail.de", "wegwerfmail.net", "wegwerfmail.org",
        "wegwerfemail.com", "wegwerfemail.de", "wegwerfemail.net", "wegwerfemail.org",
        // Zehn Minuten
        "zehnminuten.de", "zehnminutenmail.de",
        // Mailnesia
        "mailnesia.com",
        // Mohmal
        "mohmal.com",
        // Pookmail
        "pookmail.com",
        // Getairmail
        "getairmail.com",
        // Inboxbear
        "inboxbear.com",
        // Safetymail
        "safetymail.info", "safetypost.de",
        // Filzmail
        "filzmail.com",
        // Getonemail / Oneoffemail
        "getonemail.com", "oneoffemail.com", "onewaymail.com",
        // Dayrep / Einrot / Fleckens
        "dayrep.com", "einrot.com", "fleckens.hu",
        // Mintemail
        "mintemail.com",
        // Mailexpire
        "mailexpire.com",
        // Mailme / Mailscrap
        "mailme.ir", "mailmetrash.com", "mailscrap.com",
        "mailsiphon.com", "mailzilla.org",
        // Mt2009 / Mt2014
        "mt2009.com", "mt2014.com",
        // Netmails
        "netmails.net", "netmails.com",
        // Objectmail / Obobbo
        "objectmail.com", "obobbo.com",
        // Pop3.xyz
        "pop3.xyz",
        // Quickinbox
        "quickinbox.com",
        // Skeefmail
        "skeefmail.com", "slopsbox.com",
        // Snkmail / Sofimail
        "snkmail.com", "sofimail.com",
        // Trbvm
        "trbvm.com",
        // Nwytg
        "nwytg.net",
        // Tmailinator
        "tmailinator.com",
        // Supergreatmail
        "supergreatmail.com",
        // Kasmail / Klzlk
        "kasmail.com", "klzlk.com",
        // Imails
        "imails.info",
        // Rmqkr
        "rmqkr.net",
        // Odnorazovoe
        "odnorazovoe.ru",
        // Mailbox52
        "mailbox52.com",
        // Yepmail
        "yepmail.net",
        // Xagloo / Xemaps
        "xagloo.com", "xemaps.com", "xents.com", "xmaily.com", "xoxy.net",
        // Spoofmail
        "spoofmail.de",
        // Stuffmail
        "stuffmail.de",
        // Tafmail / Tagyourself
        "tafmail.com", "tagyourself.com",
        // Teewars
        "teewars.org",
        // Teleworm
        "teleworm.com", "teleworm.us",
        // Thisisnotmyrealemail
        "thisisnotmyrealemail.com",
        // Tilien
        "tilien.com",
        // Toiea
        "toiea.com",
        // Turual / Twinmail
        "turual.com", "twinmail.de",
        // Uroid
        "uroid.com",
        // Venompen
        "venompen.com",
        // Wilemail
        "wilemail.com",
        // Wuzupmail
        "wuzupmail.net",
        // Z1p
        "z1p.biz",
        // Yomail
        "yomail.info",
        // Outlawspam
        "outlawspam.com",
        // Otherinbox
        "otherinbox.com",
        // Crazymailing
        "crazymailing.com",
        // EmailFake
        "emailfake.com", "emailfake.ml",
        // MailDrop / temporary
        "tempail.com", "tempemail.net",
    )

    fun isDisposable(email: String): Boolean {
        val domain = extractDomain(email)
        return domain.isNotEmpty() && domain in disposableDomains
    }

    suspend fun isDisposableAsync(email: String): Boolean {
        if (isDisposable(email)) return true
        val domain = extractDomain(email)
        return domain.isNotEmpty() && checkDisposableApi(domain)
    }

    private fun extractDomain(email: String): String =
        email.substringAfter("@", "").lowercase().trimEnd('.')
}
