package com.example.data

enum class BodyPart(val displayName: String, val icon: String) {
    CHEST("छाती (Chest)", "🛡️"),
    BACK("पाठ (Back)", "🧗"),
    ARMS("हात (Arms & Biceps)", "💪"),
    SHOULDERS("खांदे (Shoulders)", "🥋"),
    LEGS("पाय (Legs)", "🦵"),
    ABS("पोट (Abs & Core)", "🔥")
}

enum class WorkoutEquipment(val displayName: String, val badgeText: String) {
    DUMBBELL("डंबेल व्यायाम (With Dumbbells)", "🏋️ Dumbbell"),
    BODYWEIGHT("विना-डंबेल व्यायाम (Without Dumbbells)", "🤸 No Dumbbell")
}

data class ExerciseStep(
    val stepNumber: Int,
    val titleMarathi: String,
    val descriptionMarathi: String,
    val focusTip: String,
    val visualIcon: String
)

data class WorkoutExercise(
    val id: String,
    val nameMarathi: String,
    val nameEnglish: String,
    val bodyPart: BodyPart,
    val equipment: WorkoutEquipment,
    val difficulty: String,
    val targetMuscles: String,
    val setsReps: String,
    val step1: ExerciseStep,
    val step2: ExerciseStep,
    val step3: ExerciseStep,
    val proTip: String
)

object WorkoutDatabase {
    val exercises: List<WorkoutExercise> = listOf(
        // ======================= CHEST (छाती) =======================
        WorkoutExercise(
            id = "chest_dumbbell_bench_press",
            nameMarathi = "डंबेल बेंच प्रेस (Dumbbell Bench Press)",
            nameEnglish = "Flat Dumbbell Bench Press",
            bodyPart = BodyPart.CHEST,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "संपूर्ण छाती (Pectoralis Major), खांदे आणि ट्रायसेप्स",
            setsReps = "३ सेट्स × १० ते १२ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: सज्ज व्हा (Starting Position)",
                descriptionMarathi = "बेंचवर किंवा जमिनीवर पाठीवर झोपा. दोन्ही हातांत डंबेल घेऊन छातीच्या रेषेत वर धरा. खांदे मागे खेचा आणि पाय जमिनीवर स्थिर ठेवा.",
                focusTip = "पाठीला नैसर्गिक कमान (Arch) ठेवा आणि खांदे स्थिर ठेवा.",
                visualIcon = "🛌"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: नियंत्रित खाली आणा (Lowering Control)",
                descriptionMarathi = "श्वास आत घेत डंबेल हळूहळू छातीपर्यंत खाली आणा. कोपर शरीराशी ४५ ते ६० अंशांच्या कोनात असावेत. घाई करू नका.",
                focusTip = "छातीचे स्नायू ताणले जात आहेत हे अनुभवा (Deep Stretch).",
                visualIcon = "⬇️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: वर दाबा व छाती आकुंचित करा (Push & Squeeze)",
                descriptionMarathi = "श्वास बाहेर सोडत छातीच्या ताकदीने डंबेल पुन्हा वर ढकला. वर आल्यावर १ सेकंद छातीचे स्नायू घट्ट आकुंचित (Squeeze) करा.",
                focusTip = "डंबेल एकमेकांवर आपटू नका, फक्त छातीवर ताण टिकवून ठेवा.",
                visualIcon = "⬆️"
            ),
            proTip = "डंबेल खाली आणताना २ सेकंद वेळ घ्या आणि वर ढकलताना १ सेकंदात नियंत्रित दाबा."
        ),

        WorkoutExercise(
            id = "chest_dumbbell_flyes",
            nameMarathi = "डंबेल फ्लाय (Dumbbell Fly)",
            nameEnglish = "Dumbbell Chest Fly",
            bodyPart = BodyPart.CHEST,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "छातीचा बाहेरील आणि मधला भाग (Outer & Inner Pecs)",
            setsReps = "३ सेट्स × १२ ते १५ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: पकड व सुरुवातीची स्थिती",
                descriptionMarathi = "बेंचवर झोपून दोन्ही हातांत डंबेल समोरासमोर तळहात ठेवून छातीच्या वर सरळ धरा. कोपरामध्ये हलकी घडी (Slight Bend) ठेवा.",
                focusTip = "हात पूर्णपणे ताठ ठेवू नका, कोपर किंचित वाकलेले हवेत.",
                visualIcon = "🤲"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: पंखांप्रमाणे हात बाजूला उघडा",
                descriptionMarathi = "श्वास घेत दोन्ही हात एकाच वेळी रुंद बाजूला खाली न्या, जणू तुम्ही एखाद्या मोठ्या वृक्षाला मिठी मारत आहात. छातीत संपूर्ण ताण जाणवेपर्यंत खाली आणा.",
                focusTip = "कोपर खांद्याच्या खाली जास्त झुकवू नका जेणेकरून दुखापत टळेल.",
                visualIcon = "↔️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: छाती आकुंचित करत वर आणा",
                descriptionMarathi = "श्वास सोडत डंबेल पुन्हा वर एकत्र आणा. वर आल्यावर छातीचे स्नायू आतील बाजूस घट्ट दाबा.",
                focusTip = "फक्त छातीचा वापर करून हात वर आणा, हातांचा ताण टाळा.",
                visualIcon = "🔄"
            ),
            proTip = "जास्त वजन घेण्याऐवजी योग्य फॉर्म आणि स्नायूंच्या ताणावर लक्ष केंद्रित करा."
        ),

        WorkoutExercise(
            id = "chest_bodyweight_pushups",
            nameMarathi = "स्टँडर्ड पुश-अप्स (Standard Push-Ups)",
            nameEnglish = "Bodyweight Push-Ups",
            bodyPart = BodyPart.CHEST,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "सुलभ-मध्यम (Beginner to Intermediate)",
            targetMuscles = "संपूर्ण छाती, ट्रायसेप्स, कोर आणि खांदे",
            setsReps = "३ ते ४ सेट्स × १५ ते २० रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: प्लँक पोझिशन (High Plank Setup)",
                descriptionMarathi = "दोन्ही हात जमिनीवर खांद्यापेक्षा किंचित रुंद ठेवा. पायाची बोटे जमिनीवर टेकवून डोक्यापासून पायापर्यंत शरीर एका सरळ रेषेत ठेवा.",
                focusTip = "पोट आणि नितंब घट्ट बांधून ठेवा, कंबर खाली झुकू देऊ नका.",
                visualIcon = "📏"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: शरीर खाली आणा (Lower to Chest)",
                descriptionMarathi = "श्वास घेत कोपर वाकवून छाती जमिनीपासून १ इंच अंतरावर येईपर्यंत हळूहळू खाली आणा. कोपर ४५ अंशावर ठेवा.",
                focusTip = "मान सरळ ठेवा, नजर जमिनीवर १ फूट पुढे असावी.",
                visualIcon = "📉"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: जमिनीला धक्का देऊन वर या (Explode Up)",
                descriptionMarathi = "श्वास सोडत हातांनी जमिनीला जोराने खाली दाबा आणि शरीर वर आणा. वर आल्यावर छाती घट्ट आकुंचित करा.",
                focusTip = "वर आल्यावर कोपर लॉक करू नका, हलकी लवचिकता ठेवा.",
                visualIcon = "🚀"
            ),
            proTip = "जर पूर्ण पुश-अप्स जमत नसतील तर सुरुवातीला गुडघे जमिनीवर टेकवून नी-पुशअप्स (Knee Push-ups) करा."
        ),

        WorkoutExercise(
            id = "chest_bodyweight_incline_pushups",
            nameMarathi = "इन्क्लाइन व डिक्लाइन पुश-अप्स (Incline / Decline Push-Ups)",
            nameEnglish = "Incline & Decline Bodyweight Push-Ups",
            bodyPart = BodyPart.CHEST,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "छातीचा वरचा आणि खालचा भाग (Upper & Lower Chest)",
            setsReps = "३ सेट्स × १२ ते १५ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: सोफा किंवा टेबलावर पाय/हात ठेवा",
                descriptionMarathi = "वरच्या छातीसाठी पाय सोफ्यावर (Decline) ठेवा किंवा सुरुवातीसाठी हात सोफ्यावर (Incline) ठेवा. शरीर सरळ रेषेत आणा.",
                focusTip = "शरीराचा तोल सांभाळा आणि कोअर घट्ट ठेवा.",
                visualIcon = "🪜"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: छाती जमिनीच्या दिशेने खाली न्या",
                descriptionMarathi = "श्वास आत घेत छाती जमिनीच्या दिशेने सावकाश खाली आणा. वरच्या छातीवर ताण अनुभवा.",
                focusTip = "कंबर खाली झुकणार नाही याची काळजी घ्या.",
                visualIcon = "↘️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: वर येऊन वरची छाती आकुंचित करा",
                descriptionMarathi = "श्वास सोडत स्वतःला वर ढकला आणि वरच्या छातीचे स्नायू दाबा. यामुळे छातीला उत्तम आकार मिळतो.",
                focusTip = "पूर्ण गतीमध्ये हालचाल करा (Full Range of Motion).",
                visualIcon = "↗️"
            ),
            proTip = "डिक्लाइन पुश-अप्सने छातीचा वरचा भाग (Upper Chest) मजबूत आणि भरदार बनतो."
        ),

        // ======================= ARMS (हात / बायसेप्स-ट्रायसेप्स) =======================
        WorkoutExercise(
            id = "arms_dumbbell_bicep_curl",
            nameMarathi = "डंबेल बायसेप कर्ल (Dumbbell Bicep Curl)",
            nameEnglish = "Standing Dumbbell Bicep Curl",
            bodyPart = BodyPart.ARMS,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "सुलभ (Beginner)",
            targetMuscles = "बायसेप्स (Biceps Brachii), पुढचा हात (Forearms)",
            setsReps = "३ सेट्स × १२ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: सरळ उभे राहा व डंबेल धरा",
                descriptionMarathi = "छाती पुढे, खांदे मागे ठेवून ताठ उभे राहा. दोन्ही हातांत डंबेल शरीराच्या बाजूला ठेवा, तळहात पुढे असावेत.",
                focusTip = "कोपर कंबरेच्या जवळ स्थिर ठेवा, पुढे मागे हलवू नका.",
                visualIcon = "🧍"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: डंबेल खांद्याकडे वळवा (Curl Up)",
                descriptionMarathi = "श्वास सोडत फक्त पुढचा हात (Forearms) वर उचलून डंबेल खांद्याच्या दिशेने वळवा. शरीराला झटका देऊ नका.",
                focusTip = "शरीर झुलवू नका, केवळ बायसेप्सचा ताण वापरा.",
                visualIcon = "💪"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: पीक कॉन्ट्रॅक्शन व हळूच खाली",
                descriptionMarathi = "वर आल्यावर बायसेप्स १ सेकंद घट्ट आकुंचित करा, नंतर श्वास घेत ३ सेकंदांत हळूच मूळ स्थितीत आणा.",
                focusTip = "खाली आणताना वजन सोडू नका, स्नायूंच्या नियंत्रणात खाली आणा.",
                visualIcon = "⏳"
            ),
            proTip = "वजन जास्त घेण्यापेक्षा कोपर स्थिर ठेवून बायसेप्स आकुंचित करणे सर्वात महत्त्वाचे आहे."
        ),

        WorkoutExercise(
            id = "arms_bodyweight_chair_dips",
            nameMarathi = "चेअर / बेंच ट्रायसेप डिप्स (Bodyweight Chair Dips)",
            nameEnglish = "Chair / Bench Tricep Dips",
            bodyPart = BodyPart.ARMS,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "सुलभ-मध्यम (Beginner to Intermediate)",
            targetMuscles = "ट्रायसेप्स (Triceps Brachii), खांदे आणि पाठीचा वरचा भाग",
            setsReps = "३ सेट्स × १२ ते १५ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: खुर्ची किंवा बाकड्यावर हात टेकवा",
                descriptionMarathi = "खुर्चीच्या कडेवर हात टेकवून पुढे बसा. पाय पुढे सरळ किंवा गुडघ्यात ९० अंशांवर ठेवा. नितंब खुर्चीच्या जवळ ठेवा.",
                focusTip = "हात खांद्याच्या अंतरावर स्थिर ठेवा.",
                visualIcon = "🪑"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: कोपर ९० अंश वाकवून खाली जा",
                descriptionMarathi = "श्वास घेत कोपर मागे वाकवत शरीर हळूहळू खाली न्या. कोपर ९० अंशांचा कोन होईपर्यंत खाली आणा.",
                focusTip = "शरीर खुर्चीपासून जास्त दूर नेऊ नका, खांद्यावर ताण पडू नये.",
                visualIcon = "🔽"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: ट्रायसेप्सच्या ताकदीने वर या",
                descriptionMarathi = "श्वास सोडत हातांनी खुर्चीला खाली दाबून शरीर पुन्हा वर उचला. वर आल्यावर ट्रायसेप्स घट्ट आकुंचित करा.",
                focusTip = "हात वर आल्यावर ट्रायसेप्सचे स्नायू जाणवून घट्ट करा.",
                visualIcon = "🔼"
            ),
            proTip = "पाय जितके पुढे सरळ ठेवाल तितका व्यायाम अधिक आव्हानात्मक आणि प्रभावी होतो."
        ),

        // ======================= BACK (पाठ) =======================
        WorkoutExercise(
            id = "back_dumbbell_row",
            nameMarathi = "डंबेल बेंट-ओव्हर रो (Dumbbell Bent-Over Row)",
            nameEnglish = "Single-Arm / Bent-Over Dumbbell Row",
            bodyPart = BodyPart.BACK,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "लॅट्स (Latissimus Dorsi), पाठीचा मधला भाग आणि बायसेप्स",
            setsReps = "३ सेट्स × १० ते १२ रेप्स प्रति हात",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: कंबरेतून पुढे झुका (Hinge at Hips)",
                descriptionMarathi = "गुडघे हलके वाकवून कंबरेतून ४५ अंशात पुढे झुका. पाठ पूर्णपणे सरळ आणि सपाट ठेवा. हातांत डंबेल खाली लोंबकळू द्या.",
                focusTip = "पाठ वाकडी (Round) करू नका, छाती उघडी ठेवा.",
                visualIcon = "📐"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: डंबेल बरगड्यांच्या दिशेने खेचा",
                descriptionMarathi = "श्वास सोडत कोपर मागच्या बाजूला नेऊन डंबेल कंबरेच्या/बरगड्यांच्या दिशेने वर खेचा. पाठीचे पंख एकत्र दाबा.",
                focusTip = "हातापेक्षा पाठीच्या स्नायूंचा वापर करून खेचा.",
                visualIcon = "🧗"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: पाठीचे स्नायू दाबा व हळूच खाली न्या",
                descriptionMarathi = "वर आल्यावर १ सेकंद पाठीचे स्नायू घट्ट आकुंचित करा, नंतर श्वास घेत डंबेल हळूहळू खाली आणा.",
                focusTip = "खाली नेताना पाठीच्या स्नायूंना चांगला ताण मिळू द्या.",
                visualIcon = "🔄"
            ),
            proTip = "हा व्यायाम करताना पाठीचा कणा नेहमी सरळ ठेवा जेणेकरून कंबरेवर ताण येणार नाही."
        ),

        WorkoutExercise(
            id = "back_bodyweight_superman",
            nameMarathi = "सुपरमॅन व्यायाम (Bodyweight Superman)",
            nameEnglish = "Prone Superman Hold & Pulse",
            bodyPart = BodyPart.BACK,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "सुलभ (Beginner)",
            targetMuscles = "खालची पाठ (Lower Back), ग्लूट्स आणि संपूर्ण पाठीचा कणा",
            setsReps = "३ सेट्स × १२ रेप्स (किंवा ३० सेकंद होल्ड)",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: जमिनीवर पोटावर झोपा",
                descriptionMarathi = "मॅटवर पोटावर झोपा. हात डोक्याच्या पुढे सरळ पसरवा आणि पाय मागे सरळ ठेवा. मान जमिनीकडे तटस्थ ठेवा.",
                focusTip = "शरीराला ताणून लांब करा.",
                visualIcon = "🦸"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: हात व पाय एकाच वेळी वर उचला",
                descriptionMarathi = "श्वास सोडत छाती, हात आणि पाय जमिनीवरून वर उचला, जणू तुम्ही हवेत उडत आहात. पाठीच्या स्नायूंचा ताण वापरा.",
                focusTip = "झटका न देता हळूच वर उचला.",
                visualIcon = "🛫"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: २ सेकंद थांबा व सावकाश खाली या",
                descriptionMarathi = "वरच्या स्थितीत २ सेकंद स्थिर राहा आणि पाठीचे स्नायू घट्ट दाबा, मग श्वास घेत सावकाश खाली या.",
                focusTip = "मान वर खेचू नका, पाठीच्या स्नायूंवर भर द्या.",
                visualIcon = "🛬"
            ),
            proTip = "कंबरदुखी टाळण्यासाठी आणि पाठीचा कणा ताकदवान करण्यासाठी हा अत्यंत गुणकारी व्यायाम आहे."
        ),

        // ======================= SHOULDERS (खांदे) =======================
        WorkoutExercise(
            id = "shoulders_dumbbell_overhead_press",
            nameMarathi = "डंबेल शोल्डर प्रेस (Dumbbell Overhead Press)",
            nameEnglish = "Seated / Standing Dumbbell Shoulder Press",
            bodyPart = BodyPart.SHOULDERS,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "खांद्याचे तिन्ही भाग (Anterior, Lateral & Posterior Deltoids)",
            setsReps = "३ सेट्स × १० ते १२ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: डंबेल खांद्याच्या रेषेत आणा",
                descriptionMarathi = "सरळ उभे राहा किंवा बसा. डंबेल खांद्याच्या रेषेत आणून कोपर ९० अंशात ठेवा. तळहात पुढच्या बाजूला असावेत.",
                focusTip = "कोअर घट्ट बांधा आणि कंबर स्थिर ठेवा.",
                visualIcon = "🏋️"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: डंबेल डोक्यावर वर ढकला",
                descriptionMarathi = "श्वास सोडत डंबेल डोक्यावर सरळ वर ढकला. वर हात जवळ आणा पण डंबेल एकमेकांना आपटू नका.",
                focusTip = "खांद्याचा वापर करून एका लयीत वर दाबा.",
                visualIcon = "☝️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: हळूच खांद्यापर्यंत खाली आणा",
                descriptionMarathi = "श्वास घेत ३ सेकंदांत डंबेल पुन्हा ९० अंशांवर खांद्यापर्यंत सावकाश खाली आणा. स्नायूंवर ताण टिकवून ठेवा.",
                focusTip = "डंबेल अचानक खाली पडू देऊ नका.",
                visualIcon = "👇"
            ),
            proTip = "डंबेल वर ढकलताना पाठ मागे वाकवू नका, शरीर सरळ ताठ ठेवा."
        ),

        WorkoutExercise(
            id = "shoulders_bodyweight_pike_pushups",
            nameMarathi = "पाइक पुश-अप्स (Pike Push-Ups)",
            nameEnglish = "Bodyweight Pike Push-Ups",
            bodyPart = BodyPart.SHOULDERS,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "मध्यम-प्रगत (Intermediate to Advanced)",
            targetMuscles = "खांदे (Deltoids), वरची पाठ आणि ट्रायसेप्स",
            setsReps = "३ सेट्स × ८ ते १० रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: उलटा 'V' आकार बनवा (Downward Dog)",
                descriptionMarathi = "हात जमिनीवर ठेवून नितंब वर उचला आणि उलटा 'V' आकार बनवा. पाय हातांच्या जवळ आणा आणि डोके हातांच्या मध्ये ठेवा.",
                focusTip = "वजन प्रामुख्याने खांद्यांवर ठेवा.",
                visualIcon = "∧"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: डोके हातांच्या पुढे खाली न्या",
                descriptionMarathi = "श्वास घेत कोपर वाकवून डोके हातांच्या किंचित पुढे जमिनीच्या दिशेने हळूच खाली आणा.",
                focusTip = "फक्त खांद्यांच्या जोरावर खाली वाका.",
                visualIcon = "↘️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: खांद्यांनी ढकलून मूळ स्थितीत या",
                descriptionMarathi = "श्वास सोडत हातांनी जमिनीला दाबून पुन्हा सुरुवातीच्या 'V' आकारात या. खांदे घट्ट आकुंचित करा.",
                focusTip = "संपूर्ण ताण खांद्यांवर अनुभवा.",
                visualIcon = "↖️"
            ),
            proTip = "हा व्यायाम हँडस्टँड पुश-अप्सची पूर्वतयारी आहे, यामुळे खांद्यांना अप्रतिम रुंदी मिळते."
        ),

        // ======================= LEGS (पाय) =======================
        WorkoutExercise(
            id = "legs_dumbbell_squats",
            nameMarathi = "डंबेल गॉब्लेट स्क्वॅट्स (Dumbbell Goblet Squats)",
            nameEnglish = "Dumbbell Goblet Squats",
            bodyPart = BodyPart.LEGS,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "मांडीचे स्नायू (Quadriceps, Hamstrings), ग्लूट्स आणि कोर",
            setsReps = "३ सेट्स × १२ ते १५ रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: डंबेल छातीजवळ धरा व उभे राहा",
                descriptionMarathi = "पायांमध्ये खांद्यापेक्षा किंचित जास्त अंतर ठेवून उभे राहा. एक डंबेल दोन्ही हातांनी छातीजवळ उभा धरून ठेवा.",
                focusTip = "छाती उंच ठेवा आणि पायाचे पंजे हलके बाहेर वळवा.",
                visualIcon = "🧍"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: खुर्चीवर बसल्यासारखे खाली बसा",
                descriptionMarathi = "श्वास घेत कंबर मागे काढून गुडघे वाकवा आणि मांड्या जमिनीला समांतर होईपर्यंत खाली बसा. टाचांवर भार ठेवा.",
                focusTip = "गुडघे पायाच्या अंगठ्याच्या पुढे जास्त जाऊ देऊ नका.",
                visualIcon = "🪑"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: टाचांवर जोर देऊन वर या",
                descriptionMarathi = "श्वास सोडत टाचांनी जमिनीला दाबा आणि वर या. वर आल्यावर ग्लूट्स आणि मांड्यांचे स्नायू घट्ट आकुंचित करा.",
                focusTip = "वर आल्यावर गुडघे लॉक करू नका.",
                visualIcon = "⬆️"
            ),
            proTip = "खाली जाताना पाठीचा कणा नेहमी सरळ ठेवा, नजर समोर ठेवा."
        ),

        WorkoutExercise(
            id = "legs_bodyweight_squats",
            nameMarathi = "बॉडीवेट एअर स्क्वॅट्स (Bodyweight Air Squats)",
            nameEnglish = "Bodyweight Air Squats",
            bodyPart = BodyPart.LEGS,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "सुलभ (Beginner)",
            targetMuscles = "मांड्या, नितंब (Glutes) आणि मांड्यांचा मागचा भाग",
            setsReps = "३ ते ४ सेट्स × १५ ते २० रेप्स",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: पायात अंतर ठेवून हात पुढे धरा",
                descriptionMarathi = "पाय खांद्याच्या अंतरावर ठेवा. हात समोर सरळ किंवा छातीवर बांधून ठेवा. छाती ताठ आणि मान सरळ ठेवा.",
                focusTip = "शारीरिक तोल बरोबर मधोमध ठेवा.",
                visualIcon = "🙌"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: नियंत्रित गतीने खाली उतरा",
                descriptionMarathi = "श्वास घेत कंबर मागे ढकलत गुडघ्यातून खाली वाका. मांड्या जमिनीला समांतर होईपर्यंत सावकाश खाली जा.",
                focusTip = "टाचा जमिनीवरून वर उचलू नका.",
                visualIcon = "⬇️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: पायांच्या ताकदीने सरळ उभे राहा",
                descriptionMarathi = "श्वास सोडत मांड्यांच्या स्नायूंनी स्वतःला वर ढकला. वर आल्यावर नितंब घट्ट आकुंचित करा.",
                focusTip = "स्थिर लयीत सलग रेप्स करा.",
                visualIcon = "⚡"
            ),
            proTip = "हा व्यायाम दररोज केल्याने पायांची ताकद आणि सहनशक्ती प्रचंड वाढते."
        ),

        // ======================= ABS & CORE (पोट / अ‍ॅब्स) =======================
        WorkoutExercise(
            id = "abs_bodyweight_plank",
            nameMarathi = "क्लासिक फोरआर्म प्लँक (Classic Forearm Plank)",
            nameEnglish = "Standard Forearm Plank Hold",
            bodyPart = BodyPart.ABS,
            equipment = WorkoutEquipment.BODYWEIGHT,
            difficulty = "सुलभ-मध्यम (Beginner to Intermediate)",
            targetMuscles = "संपूर्ण कोर (Rectus Abdominis, Transverse Abdominis, Obliques)",
            setsReps = "३ सेट्स × ३० ते ६० सेकंद होल्ड",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: कोपरांवर जमिनीवर टेकवा",
                descriptionMarathi = "मॅटवर झोपून कोपर खांद्यांच्या अगदी खाली ठेवा. पायाची बोटे जमिनीवर टेकवून शरीर वर उचला.",
                focusTip = "डोक्यापासून पायापर्यंत शरीर एका सपाट फळीसारखे (Plank) असावे.",
                visualIcon = "🧱"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: पोट आणि नितंब घट्ट बांधून ठेवा",
                descriptionMarathi = "नाभी मणक्याच्या दिशेने आत खेचा. पोट आणि पार्श्वभाग घट्ट आकुंचित करून ठेवा. कंबर खाली झुकू किंवा जास्त वर येऊ देऊ नका.",
                focusTip = "नियमित संथ श्वासोच्छ्वास चालू ठेवा, श्वास रोखून धरू नका.",
                visualIcon = "🔒"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: वेळ संपेपर्यंत स्थिर राहा",
                descriptionMarathi = "३० ते ६० सेकंद संपूर्ण कोरवर ताण टिकवून ठेवा. वेळ संपल्यावर सावकाश गुडघे जमिनीवर टेकवून विश्रांती घ्या.",
                focusTip = "थरथर झाली तरी फॉर्म सांभाळा, यामुळे कोअर अत्यंत मजबूत होतो.",
                visualIcon = "⏱️"
            ),
            proTip = "प्लँकमध्ये वेळ वाढवण्यापेक्षा शरीराची सरळ रेषा आणि पोटाचे आकुंचन अचूक राखणे अधिक महत्त्वाचे आहे."
        ),

        WorkoutExercise(
            id = "abs_dumbbell_russian_twist",
            nameMarathi = "डंबेल रशियन ट्विस्ट (Dumbbell Russian Twist)",
            nameEnglish = "Seated Dumbbell Russian Twist",
            bodyPart = BodyPart.ABS,
            equipment = WorkoutEquipment.DUMBBELL,
            difficulty = "मध्यम (Intermediate)",
            targetMuscles = "पोटाच्या बाजूचे स्नायू (Obliques) आणि संपूर्ण कोर",
            setsReps = "३ सेट्स × २० ट्विस्ट (१० प्रति बाजू)",
            step1 = ExerciseStep(
                stepNumber = 1,
                titleMarathi = "स्टेप १: जमिनीवर बसून ४५ अंशात मागे झुका",
                descriptionMarathi = "जमिनीवर बसून गुडघे किंचित वाकवा. कंबरेतून ४५ अंशात मागे झुका. दोन्ही हातांत एक डंबेल छातीसमोर धरा.",
                focusTip = "आव्हान वाढवण्यासाठी पाय जमिनीपासून २ इंच वर उचला.",
                visualIcon = "💺"
            ),
            step2 = ExerciseStep(
                stepNumber = 2,
                titleMarathi = "स्टेप २: डंबेल एका बाजूला वळवा (Twist Left)",
                descriptionMarathi = "श्वास सोडत डंबेल आणि धड डाव्या बाजूला वळवा, डंबेल जमिनीजवळ आणा. पोटाच्या बाजूला ताण अनुभवा.",
                focusTip = "फक्त हात हलवू नका, संपूर्ण छाती आणि धड वळवा.",
                visualIcon = "↩️"
            ),
            step3 = ExerciseStep(
                stepNumber = 3,
                titleMarathi = "स्टेप ३: दुसरीकडे वळवा व संतुलन राखा (Twist Right)",
                descriptionMarathi = "नियंत्रित गतीने उजव्या बाजूला फिरा आणि डंबेल उजवीकडे आणा. दोन्ही बाजूंनी समान ताण द्या.",
                focusTip = "पोटाचे स्नायू सतत घट्ट ठेवा.",
                visualIcon = "↪️"
            ),
            proTip = "कंबरेच्या बाजूची चरबी (Love Handles) कमी करण्यासाठी आणि साइड अ‍ॅब्ससाठी हा सर्वोत्तम व्यायाम आहे."
        )
    )
}
