package com.mhma.nibras.content

/** Language a lecture channel mainly speaks. */
enum class LectureLang { EN, AR, RECITATION }

class Lecture(
    val name: Localized,
    val description: Localized,
    val topics: Localized,
    val url: String,
    val lang: LectureLang,
    /** Palette index used for the avatar colour. */
    val palette: Int
)

/**
 * Curated official channels of well-known scholars and institutions.
 * Every entry links to the channel itself, so the links stay valid even
 * as new videos are published.
 */
object Lectures {

    val items: List<Lecture> = listOf(
        // ---- English -------------------------------------------------------
        Lecture(
            Localized("Yaqeen Institute", "معهد يقين"),
            Localized(
                "Research-based lectures and series by Dr. Omar Suleiman and a team of scholars.",
                "محاضرات وسلاسل مبنية على البحث للدكتور عمر سليمان وفريق من العلماء."
            ),
            Localized("Faith · Seerah · Names of Allah", "الإيمان · السيرة · أسماء الله"),
            "https://www.youtube.com/@yaqeeninstituteofficial", LectureLang.EN, 0
        ),
        Lecture(
            Localized("Mufti Menk", "المفتي منك"),
            Localized(
                "Warm, practical reminders on faith, family and everyday life.",
                "تذكيرات عملية دافئة عن الإيمان والأسرة والحياة اليومية."
            ),
            Localized("Motivation · Character · Family", "تحفيز · أخلاق · أسرة"),
            "https://www.youtube.com/@muftimenkofficial", LectureLang.EN, 1
        ),
        Lecture(
            Localized("Nouman Ali Khan — Bayyinah", "نعمان علي خان — بيّنة"),
            Localized(
                "Deep reflections on the language and meanings of the Qur'an.",
                "تأملات عميقة في لغة القرآن ومعانيه."
            ),
            Localized("Qur'an · Tafsir · Arabic", "القرآن · التفسير · العربية"),
            "https://www.youtube.com/@bayyinah", LectureLang.EN, 2
        ),
        Lecture(
            Localized("Dr. Yasir Qadhi", "د. ياسر قاضي"),
            Localized(
                "Detailed lecture series on the Seerah, theology and Islamic history.",
                "سلاسل محاضرات مفصلة في السيرة والعقيدة والتاريخ الإسلامي."
            ),
            Localized("Seerah · History · Theology", "السيرة · التاريخ · العقيدة"),
            "https://www.youtube.com/@YasirQadhi", LectureLang.EN, 3
        ),
        Lecture(
            Localized("Sheikh Assim Al-Hakeem", "الشيخ عاصم الحكيم"),
            Localized(
                "Clear, concise answers to everyday questions about worship and fiqh.",
                "إجابات واضحة وموجزة عن أسئلة العبادة والفقه اليومية."
            ),
            Localized("Fiqh · Q&A · Worship", "الفقه · أسئلة وأجوبة · العبادات"),
            "https://www.youtube.com/@assimalhakeem", LectureLang.EN, 4
        ),
        Lecture(
            Localized("Jannah Institute — Dr. Haifaa Younis", "معهد جنّة — د. هيفاء يونس"),
            Localized(
                "Tafsir, purification of the heart and classes designed for women.",
                "تفسير وتزكية للقلب ودروس مصممة للنساء."
            ),
            Localized("Tafsir · Spirituality · Women", "التفسير · الروحانية · المرأة"),
            "https://www.youtube.com/c/JannahInstitute", LectureLang.EN, 5
        ),
        Lecture(
            Localized("Shaykh Hamza Yusuf", "الشيخ حمزة يوسف"),
            Localized(
                "Classical scholarship, ethics and the intellectual heritage of Islam.",
                "العلم الشرعي الكلاسيكي والأخلاق والتراث الفكري للإسلام."
            ),
            Localized("Ethics · Classical texts · Reflection", "الأخلاق · النصوص الكلاسيكية · التأمل"),
            "https://www.youtube.com/@SandalaMediaCenter", LectureLang.EN, 6
        ),
        Lecture(
            Localized("Dr. Bilal Philips", "د. بلال فيليبس"),
            Localized(
                "Foundations of Islamic studies and creed for new and returning Muslims.",
                "أسس الدراسات الإسلامية والعقيدة للمسلمين الجدد والعائدين."
            ),
            Localized("Creed · Foundations · New Muslims", "العقيدة · الأسس · المسلمون الجدد"),
            "https://www.youtube.com/user/aabphilips", LectureLang.EN, 7
        ),

        // ---- Arabic --------------------------------------------------------
        Lecture(
            Localized("Sheikh Muhammad ibn Salih al-Uthaymeen", "الشيخ محمد بن صالح العثيمين"),
            Localized(
                "The official channel: explanations of classical texts, tafsir and fatwas.",
                "القناة الرسمية: شروح المتون الكلاسيكية والتفسير والفتاوى."
            ),
            Localized("Fiqh · Tafsir · Creed", "الفقه · التفسير · العقيدة"),
            "https://www.youtube.com/@ibnothaimeentv", LectureLang.AR, 0
        ),
        Lecture(
            Localized("Sheikh Abdul-Aziz ibn Baz", "الشيخ عبد العزيز بن باز"),
            Localized(
                "Lectures, fatwas and advice from the late Grand Mufti's official archive.",
                "محاضرات وفتاوى وتوجيهات من الأرشيف الرسمي لسماحة المفتي الراحل."
            ),
            Localized("Fatwas · Creed · Advice", "الفتاوى · العقيدة · التوجيهات"),
            "https://www.youtube.com/channel/UCiiJRwQ0MUaQo8ZZuf18pPw", LectureLang.AR, 1
        ),
        Lecture(
            Localized("Sheikh Salih al-Fawzan", "الشيخ صالح الفوزان"),
            Localized(
                "Lessons in creed and fiqh from a member of the Council of Senior Scholars.",
                "دروس في العقيدة والفقه لعضو هيئة كبار العلماء."
            ),
            Localized("Creed · Fiqh · Lessons", "العقيدة · الفقه · الدروس"),
            "https://www.youtube.com/@dralfawzann", LectureLang.AR, 2
        ),
        Lecture(
            Localized("Sheikh Muhammad Metwalli al-Sha'rawi", "الشيخ محمد متولي الشعراوي"),
            Localized(
                "The beloved tafsir sessions and reflections of the late Egyptian scholar.",
                "خواطر التفسير المحبوبة وتأملات الشيخ المصري الراحل."
            ),
            Localized("Tafsir · Reflections", "التفسير · الخواطر"),
            "https://www.youtube.com/@alsharawiofficial", LectureLang.AR, 3
        ),
        Lecture(
            Localized("Dr. Muhammad Ratib al-Nabulsi", "د. محمد راتب النابلسي"),
            Localized(
                "The official encyclopedia channel: faith, the names of Allah and the miracles of creation.",
                "القناة الرسمية للموسوعة: الإيمان وأسماء الله الحسنى والإعجاز في الخلق."
            ),
            Localized("Faith · Names of Allah · Creation", "الإيمان · أسماء الله · الخلق"),
            "https://www.youtube.com/@nabulsiencyclopedia", LectureLang.AR, 4
        ),
        Lecture(
            Localized("Sheikh Salih al-Maghamsi", "الشيخ صالح المغامسي"),
            Localized(
                "Eloquent reflections on the Qur'an, the Seerah and the stories of the prophets.",
                "تأملات بليغة في القرآن والسيرة وقصص الأنبياء."
            ),
            Localized("Qur'an · Seerah · Stories", "القرآن · السيرة · القصص"),
            "https://www.youtube.com/@Alrasekhoon", LectureLang.AR, 5
        ),
        Lecture(
            Localized("Dr. Omar Abdul Kafi", "د. عمر عبد الكافي"),
            Localized(
                "Heart-softening programmes on faith, the Hereafter and daily conduct.",
                "برامج ترقّق القلوب عن الإيمان والآخرة والسلوك اليومي."
            ),
            Localized("Faith · Hereafter · Conduct", "الإيمان · الآخرة · السلوك"),
            "https://www.youtube.com/@abdelkafytube", LectureLang.AR, 6
        ),
        Lecture(
            Localized("Sheikh Muhammad al-Mukhtar al-Shinqiti", "الشيخ محمد المختار الشنقيطي"),
            Localized(
                "Moving lessons on fiqh, manners and the purification of the soul.",
                "دروس مؤثرة في الفقه والآداب وتزكية النفس."
            ),
            Localized("Fiqh · Manners · Soul", "الفقه · الآداب · النفس"),
            "https://www.youtube.com/channel/UCE9ktIxh_9neyrQp8xHTnpw", LectureLang.AR, 7
        ),

        // ---- Recitation ------------------------------------------------------
        Lecture(
            Localized("Mishary Rashid Alafasy", "مشاري راشد العفاسي"),
            Localized(
                "Complete Qur'an recitations in a voice loved around the world.",
                "تلاوات كاملة للقرآن الكريم بصوت محبوب حول العالم."
            ),
            Localized("Qur'an recitation", "تلاوة القرآن"),
            "https://www.youtube.com/@alafasy", LectureLang.RECITATION, 0
        )
    )
}
