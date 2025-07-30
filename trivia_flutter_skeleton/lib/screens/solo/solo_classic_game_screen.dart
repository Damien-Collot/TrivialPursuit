import 'package:flutter/material.dart';
import 'package:trivia/core/constants/colors.dart';
import 'package:trivia/services/game_service.dart';
import 'package:html_unescape/html_unescape.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SoloClassicGameScreen extends StatefulWidget {
  final int questionLimit;
  final String? difficulty;
  final List<int>? categories;

  const SoloClassicGameScreen({
    super.key,
    required this.questionLimit,
    this.difficulty,
    this.categories,
  });

  @override
  State<SoloClassicGameScreen> createState() => _SoloClassicGameScreenState();
}

class _SoloClassicGameScreenState extends State<SoloClassicGameScreen> {
  final GameService _gameService = GameService();
  final unescape = HtmlUnescape();

  int score = 0;
  int questionCount = 0;
  int? sessionId;
  Map<String, dynamic>? currentQuestion;
  bool loading = true;

  Future<int?> _getUserId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt("userId");
  }

  @override
  void initState() {
    super.initState();
    startGame();
  }

  Future<void> startGame() async {
    setState(() => loading = true);
    final userId = await _getUserId();
    if (userId == null) return;

    final session = await _gameService.startSoloClassicSession(
      userId: userId,
      questionLimit: widget.questionLimit,
      difficulty: widget.difficulty,
      categories: widget.categories,
    );

    if (session == null) return;

    sessionId = session["id"];
    await fetchNextQuestion();
  }

  Future<void> fetchNextQuestion() async {
    if (sessionId == null) return;

    setState(() => loading = true);
    final question = await _gameService.fetchNextQuestion(sessionId!);
    setState(() {
      currentQuestion = question;
      loading = false;
    });
  }

  void submitAnswer(String answer) async {
    if (sessionId == null) return;

    final result = await _gameService.submitAnswer(
      sessionId: sessionId!,
      userAnswer: answer,
    );

    if (result == null) return;

    if (result["correct"] == true) score++;

    if (result["ended"] == true) {
      showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text("🎉 Partie terminée !"),
          content: Text("Votre score : $score / ${widget.questionLimit}"),
          actions: [
            TextButton(
              onPressed: () => Navigator.popUntil(context, (r) => r.isFirst),
              child: const Text("Retour au menu"),
            )
          ],
        ),
      );
    } else {
      setState(() => questionCount++);
      await fetchNextQuestion();
    }
  }

  @override
  Widget build(BuildContext context) {
    if (loading || currentQuestion == null) {
      return const Scaffold(
        backgroundColor: AppColors.background,
        body: Center(child: CircularProgressIndicator()),
      );
    }

    final question = unescape.convert(currentQuestion!["question"]);
    final answers = <String>[
      ...?currentQuestion!["incorrectAnswers"]?.map(unescape.convert),
      unescape.convert(currentQuestion!["correctAnswer"]),
    ]..shuffle();

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: Text("Question ${questionCount + 1} / ${widget.questionLimit}"),
        actions: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Text("Score: $score"),
          )
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              question,
              style: const TextStyle(fontSize: 22, color: AppColors.text),
            ),
            const SizedBox(height: 30),
            ...answers.map(
              (a) => Padding(
                padding: const EdgeInsets.symmetric(vertical: 6.0),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    minimumSize: const Size.fromHeight(50),
                  ),
                  onPressed: () => submitAnswer(a),
                  child: Text(a, style: const TextStyle(fontSize: 16, color: AppColors.text)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
