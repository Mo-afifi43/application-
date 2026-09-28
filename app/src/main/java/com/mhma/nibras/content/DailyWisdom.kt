package com.mhma.nibras.content

import java.util.Calendar

/** A short reminder shown on the home screen; rotates every day. */
class Wisdom(val text: Localized, val source: Localized)

object DailyWisdom {

    fun today(): Wisdom {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return items[day % items.size]
    }

    val items: List<Wisdom> = listOf(
        Wisdom(
            Localized(
                "Actions are judged by intentions, and every person will have what they intended.",
                "إنما الأعمال بالنيات، وإنما لكل امرئ ما نوى."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "Verily, with hardship comes ease.",
                "إِنَّ مَعَ الْعُسْرِ يُسْرًا"
            ),
            Localized("Qur'an 94:6", "سورة الشرح: ٦")
        ),
        Wisdom(
            Localized(
                "The strong person is not the one who overpowers others, but the one who controls himself when angry.",
                "ليس الشديد بالصُّرَعة، إنما الشديد الذي يملك نفسه عند الغضب."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
                "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ"
            ),
            Localized("Qur'an 2:152", "سورة البقرة: ١٥٢")
        ),
        Wisdom(
            Localized(
                "The best of you are those who are best to their families.",
                "خيركم خيركم لأهله."
            ),
            Localized("The Prophet ﷺ — Tirmidhi", "النبي ﷺ — رواه الترمذي")
        ),
        Wisdom(
            Localized(
                "Whoever treads a path in search of knowledge, Allah makes easy for him a path to Paradise.",
                "من سلك طريقًا يلتمس فيه علمًا سهّل الله له به طريقًا إلى الجنة."
            ),
            Localized("The Prophet ﷺ — Muslim", "النبي ﷺ — رواه مسلم")
        ),
        Wisdom(
            Localized(
                "And whoever relies upon Allah, then He is sufficient for him.",
                "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ"
            ),
            Localized("Qur'an 65:3", "سورة الطلاق: ٣")
        ),
        Wisdom(
            Localized(
                "A kind word is charity.",
                "الكلمة الطيبة صدقة."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "Be in this world as if you were a stranger or a traveller passing through.",
                "كن في الدنيا كأنك غريب أو عابر سبيل."
            ),
            Localized("The Prophet ﷺ — Bukhari", "النبي ﷺ — رواه البخاري")
        ),
        Wisdom(
            Localized(
                "Do not be sad; indeed Allah is with us.",
                "لَا تَحْزَنْ إِنَّ اللَّهَ مَعَنَا"
            ),
            Localized("Qur'an 9:40", "سورة التوبة: ٤٠")
        ),
        Wisdom(
            Localized(
                "None of you truly believes until he loves for his brother what he loves for himself.",
                "لا يؤمن أحدكم حتى يحب لأخيه ما يحب لنفسه."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "Whoever does not thank people has not thanked Allah.",
                "من لا يشكر الناس لا يشكر الله."
            ),
            Localized("The Prophet ﷺ — Abu Dawud & Tirmidhi", "النبي ﷺ — رواه أبو داود والترمذي")
        ),
        Wisdom(
            Localized(
                "Unquestionably, by the remembrance of Allah hearts are assured.",
                "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ"
            ),
            Localized("Qur'an 13:28", "سورة الرعد: ٢٨")
        ),
        Wisdom(
            Localized(
                "The most beloved deeds to Allah are those done regularly, even if they are small.",
                "أحب الأعمال إلى الله أدومها وإن قلّ."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "Make things easy and do not make them difficult; give glad tidings and do not drive people away.",
                "يسّروا ولا تعسّروا، وبشّروا ولا تنفّروا."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "And My mercy encompasses all things.",
                "وَرَحْمَتِي وَسِعَتْ كُلَّ شَيْءٍ"
            ),
            Localized("Qur'an 7:156", "سورة الأعراف: ١٥٦")
        ),
        Wisdom(
            Localized(
                "Smiling at your brother is charity.",
                "تبسّمك في وجه أخيك لك صدقة."
            ),
            Localized("The Prophet ﷺ — Tirmidhi", "النبي ﷺ — رواه الترمذي")
        ),
        Wisdom(
            Localized(
                "Allah does not look at your appearance or your wealth, but He looks at your hearts and your deeds.",
                "إن الله لا ينظر إلى صوركم وأموالكم، ولكن ينظر إلى قلوبكم وأعمالكم."
            ),
            Localized("The Prophet ﷺ — Muslim", "النبي ﷺ — رواه مسلم")
        ),
        Wisdom(
            Localized(
                "Indeed, Allah is with the patient.",
                "إِنَّ اللَّهَ مَعَ الصَّابِرِينَ"
            ),
            Localized("Qur'an 2:153", "سورة البقرة: ١٥٣")
        ),
        Wisdom(
            Localized(
                "Two blessings many people are deceived about: good health and free time.",
                "نعمتان مغبون فيهما كثير من الناس: الصحة والفراغ."
            ),
            Localized("The Prophet ﷺ — Bukhari", "النبي ﷺ — رواه البخاري")
        ),
        Wisdom(
            Localized(
                "Whoever believes in Allah and the Last Day, let him speak good or remain silent.",
                "من كان يؤمن بالله واليوم الآخر فليقل خيرًا أو ليصمت."
            ),
            Localized("The Prophet ﷺ — Bukhari & Muslim", "النبي ﷺ — متفق عليه")
        ),
        Wisdom(
            Localized(
                "Call upon Me; I will respond to you.",
                "ادْعُونِي أَسْتَجِبْ لَكُمْ"
            ),
            Localized("Qur'an 40:60", "سورة غافر: ٦٠")
        ),
        Wisdom(
            Localized(
                "Fear Allah wherever you are, follow a bad deed with a good one to wipe it out, and treat people with good character.",
                "اتق الله حيثما كنت، وأتبع السيئة الحسنة تمحها، وخالق الناس بخلق حسن."
            ),
            Localized("The Prophet ﷺ — Tirmidhi", "النبي ﷺ — رواه الترمذي")
        ),
        Wisdom(
            Localized(
                "Your Lord has not forsaken you, nor has He detested you.",
                "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ"
            ),
            Localized("Qur'an 93:3", "سورة الضحى: ٣")
        ),
        Wisdom(
            Localized(
                "The merciful are shown mercy by the Most Merciful. Be merciful to those on earth, and the One in heaven will be merciful to you.",
                "الراحمون يرحمهم الرحمن، ارحموا من في الأرض يرحمكم من في السماء."
            ),
            Localized("The Prophet ﷺ — Tirmidhi & Abu Dawud", "النبي ﷺ — رواه الترمذي وأبو داود")
        ),
        Wisdom(
            Localized(
                "Charity does not decrease wealth.",
                "ما نقصت صدقة من مال."
            ),
            Localized("The Prophet ﷺ — Muslim", "النبي ﷺ — رواه مسلم")
        ),
        Wisdom(
            Localized(
                "And whoever fears Allah, He will make for him a way out.",
                "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا"
            ),
            Localized("Qur'an 65:2", "سورة الطلاق: ٢")
        ),
        Wisdom(
            Localized(
                "Take advantage of five before five: your youth before old age, your health before sickness, your wealth before poverty, your free time before you are busy, and your life before your death.",
                "اغتنم خمسًا قبل خمس: شبابك قبل هرمك، وصحتك قبل سقمك، وغناك قبل فقرك، وفراغك قبل شغلك، وحياتك قبل موتك."
            ),
            Localized("The Prophet ﷺ — al-Hakim", "النبي ﷺ — رواه الحاكم")
        ),
        Wisdom(
            Localized(
                "Speak good to people.",
                "وَقُولُوا لِلنَّاسِ حُسْنًا"
            ),
            Localized("Qur'an 2:83", "سورة البقرة: ٨٣")
        ),
        Wisdom(
            Localized(
                "Whoever relieves a believer of a hardship in this world, Allah will relieve him of a hardship on the Day of Resurrection.",
                "من نفّس عن مؤمن كربة من كرب الدنيا نفّس الله عنه كربة من كرب يوم القيامة."
            ),
            Localized("The Prophet ﷺ — Muslim", "النبي ﷺ — رواه مسلم")
        )
    )
}
