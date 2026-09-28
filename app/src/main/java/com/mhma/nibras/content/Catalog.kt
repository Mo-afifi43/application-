package com.mhma.nibras.content

/**
 * The library catalogue: every category and book with its bilingual
 * metadata. The texts themselves live in `assets/books/<id>.<lang>.txt`.
 */
object Catalog {

    val categories: List<Category> = listOf(
        Category(
            "quran",
            Localized("The Qur'an", "القرآن الكريم"),
            Localized("Understanding the Book of Allah", "فهم كتاب الله"),
            "book_open", 0
        ),
        Category(
            "hadith",
            Localized("Hadith", "الحديث النبوي"),
            Localized("The words of the Prophet ﷺ", "كلام النبي ﷺ"),
            "scroll", 1
        ),
        Category(
            "aqeedah",
            Localized("Faith & Creed", "العقيدة والإيمان"),
            Localized("What every Muslim believes", "ما يؤمن به كل مسلم"),
            "kaaba", 2
        ),
        Category(
            "prophets",
            Localized("Prophets", "الأنبياء"),
            Localized("Stories of the messengers", "قصص المرسلين"),
            "crescent", 4
        ),
        Category(
            "companions",
            Localized("Companions", "الصحابة"),
            Localized("Stars of guidance", "نجوم الهدى"),
            "people", 3
        ),
        Category(
            "spirituality",
            Localized("Heart & Soul", "القلب والروح"),
            Localized("Purification, remembrance and manners", "تزكية وذكر وآداب"),
            "tasbih", 5
        ),
        Category(
            "worship",
            Localized("Worship", "العبادات"),
            Localized("How to pray, fast and give", "كيف تصلي وتصوم وتزكّي"),
            "mosque", 6
        ),
        Category(
            "history",
            Localized("History", "التاريخ"),
            Localized("Caliphs and civilisation", "الخلفاء والحضارة"),
            "history", 7
        )
    )

    private const val NIBRAS_EN = "Nibras Library"
    private const val NIBRAS_AR = "مكتبة نبراس"

    val books: List<Book> = listOf(
        Book(
            id = "quran_intro",
            categoryId = "quran",
            title = Localized("Understanding the Qur'an", "فهم القرآن الكريم"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "An accessible introduction to the Book of Allah: how it was revealed and preserved, how to approach it with the right manners, and a closer look at Surat al-Fatihah, Ayat al-Kursi and the three protecting surahs.",
                "مدخل ميسّر إلى كتاب الله: كيف نزل وكيف حُفظ، وكيف نتعامل معه بالآداب الصحيحة، مع وقفات مع سورة الفاتحة وآية الكرسي والمعوذات."
            ),
            icon = "book_open", palette = 0, chapterCount = 7, featured = false
        ),
        Book(
            id = "nawawi40",
            categoryId = "hadith",
            title = Localized("The Forty Hadith", "الأربعون النووية"),
            author = Localized("Imam al-Nawawi", "الإمام النووي"),
            description = Localized(
                "The most beloved short collection in Islam: forty-two hadith chosen by Imam al-Nawawi because each one is a foundation of the religion. Every hadith is given in Arabic and English with a short, clear explanation.",
                "أحب المجموعات المختصرة في الإسلام: اثنان وأربعون حديثًا اختارها الإمام النووي لأن كل حديث منها أصل من أصول الدين. يُعرض كل حديث بالعربية والإنجليزية مع شرح موجز واضح."
            ),
            icon = "scroll", palette = 1, chapterCount = 42, featured = true
        ),
        Book(
            id = "hadith_gems",
            categoryId = "hadith",
            title = Localized("Gems of Prophetic Wisdom", "جواهر الحكمة النبوية"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Authentic sayings of the Prophet ﷺ arranged by theme — the heart, kindness, the tongue, family, wealth and patience — each followed by a practical lesson for daily life.",
                "أحاديث صحيحة للنبي ﷺ مرتبة حسب الموضوع: القلب، والرحمة، واللسان، والأسرة، والمال، والصبر، يتبع كل حديث درس عملي للحياة اليومية."
            ),
            icon = "quote", palette = 2, chapterCount = 6, featured = false
        ),
        Book(
            id = "pillars",
            categoryId = "aqeedah",
            title = Localized("Pillars of Islam & Faith", "أركان الإسلام والإيمان"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Built around the famous Hadith of Jibril, this book walks through the five pillars of Islam, the six pillars of faith and the station of Ihsan — what they mean and how they shape a believer's life.",
                "كتاب مبني على حديث جبريل المشهور، يمرّ بأركان الإسلام الخمسة وأركان الإيمان الستة ومقام الإحسان: معانيها وكيف تصوغ حياة المؤمن."
            ),
            icon = "kaaba", palette = 2, chapterCount = 10, featured = false
        ),
        Book(
            id = "names",
            categoryId = "aqeedah",
            title = Localized("The Beautiful Names of Allah", "أسماء الله الحسنى"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Knowing Allah by His Names is the sweetest knowledge. These chapters explore His mercy, majesty, knowledge, forgiveness and generosity — and how each name changes the way we live and pray.",
                "معرفة الله بأسمائه أحلى العلوم. تستكشف هذه الفصول رحمته وجلاله وعلمه ومغفرته وكرمه، وكيف يغيّر كل اسم طريقة حياتنا ودعائنا."
            ),
            icon = "star8", palette = 5, chapterCount = 6, featured = false
        ),
        Book(
            id = "prophets",
            categoryId = "prophets",
            title = Localized("Stories of the Prophets", "قصص الأنبياء"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "From Adam to Isa, peace be upon them all: the stories of the prophets as told in the Qur'an and the authentic Sunnah, retold simply, with the lessons each story carries for us today.",
                "من آدم إلى عيسى عليهم السلام: قصص الأنبياء كما وردت في القرآن والسنة الصحيحة، مروية ببساطة مع العبر التي تحملها كل قصة لنا اليوم."
            ),
            icon = "sparkle", palette = 4, chapterCount = 12, featured = true
        ),
        Book(
            id = "seerah",
            categoryId = "prophets",
            title = Localized("The Life of the Prophet ﷺ", "السيرة النبوية"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "The life of Muhammad ﷺ from his birth in Makkah to his farewell in Madinah: the revelation, the years of hardship, the migration, the great events and the character that changed the world.",
                "حياة محمد ﷺ من مولده في مكة إلى وداعه في المدينة: الوحي، وسنوات الشدة، والهجرة، والأحداث الكبرى، والخُلق الذي غيّر العالم."
            ),
            icon = "crescent", palette = 0, chapterCount = 10, featured = true
        ),
        Book(
            id = "sahaba",
            categoryId = "companions",
            title = Localized("Stars of the Companions", "نجوم الصحابة"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Portraits of ten companions of the Prophet ﷺ — the truthful, the just, the generous, the brave and the devoted — and the moments that made them who they were.",
                "صور من حياة عشرة من صحابة النبي ﷺ: الصدّيق والعادل والكريم والشجاع والمخلص، واللحظات التي صنعت منهم ما كانوا عليه."
            ),
            icon = "people", palette = 3, chapterCount = 10, featured = true
        ),
        Book(
            id = "women",
            categoryId = "companions",
            title = Localized("Women of Light", "نساء من نور"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Khadijah, Aisha, Fatimah, Sumayyah, Asma and Nusaybah: the women who supported the message with their wealth, knowledge, patience and courage.",
                "خديجة وعائشة وفاطمة وسمية وأسماء ونسيبة: نساء نصرن الرسالة بأموالهن وعلمهن وصبرهن وشجاعتهن."
            ),
            icon = "favorite", palette = 6, chapterCount = 6, featured = false
        ),
        Book(
            id = "heart",
            categoryId = "spirituality",
            title = Localized("Purification of the Heart", "تزكية القلب"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Sincerity, repentance, patience, gratitude, trust, humility, remembrance and love: eight stations of the heart, each explained with verses, hadith and gentle practical steps.",
                "الإخلاص والتوبة والصبر والشكر والتوكل والتواضع والذكر والمحبة: ثماني منازل للقلب، تُشرح كل منها بالآيات والأحاديث وخطوات عملية لطيفة."
            ),
            icon = "favorite", palette = 5, chapterCount = 8, featured = true
        ),
        Book(
            id = "adhkar",
            categoryId = "spirituality",
            title = Localized("Fortress of the Believer", "حصن المؤمن"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "The daily remembrances and supplications taught by the Prophet ﷺ — morning and evening, sleep and waking, home, food, the mosque, distress and forgiveness — with Arabic text, translation and source.",
                "الأذكار والأدعية اليومية التي علّمها النبي ﷺ: الصباح والمساء، والنوم والاستيقاظ، والبيت، والطعام، والمسجد، والكرب، والاستغفار، مع النص والمصدر."
            ),
            icon = "tasbih", palette = 1, chapterCount = 8, featured = true
        ),
        Book(
            id = "manners",
            categoryId = "spirituality",
            title = Localized("Prophetic Manners", "الآداب النبوية"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Truthfulness, greeting, parents, neighbours, eating, visiting the sick, anger and modesty: the everyday etiquette of the Prophet ﷺ and why it makes life beautiful.",
                "الصدق والسلام والوالدان والجيران والطعام وعيادة المريض والغضب والحياء: آداب النبي ﷺ اليومية ولماذا تجعل الحياة جميلة."
            ),
            icon = "sparkle", palette = 4, chapterCount = 8, featured = false
        ),
        Book(
            id = "worship",
            categoryId = "worship",
            title = Localized("Worship Made Easy", "العبادات الميسّرة"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "A clear, step-by-step guide to purification, prayer, Friday prayer, fasting, zakat and Hajj, written for anyone who wants to worship correctly and with presence of heart.",
                "دليل واضح خطوة بخطوة للطهارة والصلاة والجمعة والصيام والزكاة والحج، كُتب لكل من يريد أن يعبد الله على الوجه الصحيح بحضور قلب."
            ),
            icon = "mosque", palette = 6, chapterCount = 8, featured = false
        ),
        Book(
            id = "caliphs",
            categoryId = "history",
            title = Localized("The Rightly Guided Caliphs", "الخلفاء الراشدون"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "The thirty years after the Prophet ﷺ: how Abu Bakr held the community together, how Umar built a just state, how Uthman preserved the Qur'an and how Ali and Hasan faced trials with faith.",
                "ثلاثون عامًا بعد النبي ﷺ: كيف جمع أبو بكر الأمة، وكيف بنى عمر دولة العدل، وكيف حفظ عثمان القرآن، وكيف واجه علي والحسن الفتن بالإيمان."
            ),
            icon = "history", palette = 7, chapterCount = 5, featured = false
        ),
        Book(
            id = "civilization",
            categoryId = "history",
            title = Localized("Lights of Islamic Civilisation", "أنوار الحضارة الإسلامية"),
            author = Localized(NIBRAS_EN, NIBRAS_AR),
            description = Localized(
                "Knowledge as worship: the scholars of hadith, the four Imams, the scientists of the golden age, the House of Wisdom, al-Andalus and the endowments that served humanity for centuries.",
                "العلم عبادة: علماء الحديث، والأئمة الأربعة، وعلماء العصر الذهبي، وبيت الحكمة، والأندلس، والأوقاف التي خدمت الإنسانية قرونًا."
            ),
            icon = "lantern", palette = 2, chapterCount = 5, featured = false
        )
    )

    private val bookIndex: Map<String, Book> = books.associateBy { it.id }
    private val categoryIndex: Map<String, Category> = categories.associateBy { it.id }

    fun bookById(id: String): Book? = bookIndex[id]
    fun categoryById(id: String): Category? = categoryIndex[id]
}
