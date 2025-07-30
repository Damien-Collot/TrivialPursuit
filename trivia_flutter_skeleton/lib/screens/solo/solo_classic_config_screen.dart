// lib/screens/solo/solo_classic_config_screen.dart

import 'package:flutter/material.dart';
import 'package:trivia/core/constants/colors.dart';
import 'package:trivia/services/game_service.dart';

class SoloClassicConfigScreen extends StatefulWidget {
  final int currentUserId;
  const SoloClassicConfigScreen({super.key, required this.currentUserId});

  @override
  State<SoloClassicConfigScreen> createState() => _SoloClassicConfigScreenState();
}

class _SoloClassicConfigScreenState extends State<SoloClassicConfigScreen> {
  int _questionCount = 10;
  String? _selectedDifficulty = 'easy';
  final Set<int> _selectedCategories = {};

  final List<String> difficulties = ['easy', 'medium', 'hard'];
  final Map<int, String> categoryMap = {
    9: 'General Knowledge',
    10: 'Books',
    11: 'Film',
    12: 'Music',
    14: 'Television',
    17: 'Science & Nature',
    18: 'Computers',
    19: 'Mathematics',
    21: 'Sports',
    22: 'Geography',
    23: 'History',
    24: 'Politics',
    27: 'Animals'
  };

  final GameService _gameService = GameService();
  bool _loading = false;

  void _startGame() async {
    setState(() => _loading = true);

    final sessionId = await _gameService.createSoloClassicGame(
      userId: widget.currentUserId,
      questionLimit: _questionCount,
      difficulty: _selectedDifficulty,
      categories: _selectedCategories.toList(),
    );

    setState(() => _loading = false);

    if (sessionId != null) {
      Navigator.pushNamed(
        context,
        '/soloClassicGame',
        extra: {
            'questionLimit': _questionCount,
            'difficulty': _selectedDifficulty,
            'categories': _selectedCategories.toList(),
        },
     );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Erreur lors de la création de la session')),
      );
    }
  }

  InputDecoration _inputDecoration(String label) {
    return InputDecoration(
      labelText: label,
      labelStyle: const TextStyle(color: AppColors.text),
      enabledBorder: const OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.secondary),
      ),
      focusedBorder: const OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.primary, width: 2),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('Solo Classique'),
        backgroundColor: AppColors.primary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Nombre de questions
            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Nombre de questions', style: TextStyle(color: AppColors.text)),
            ),
            Slider(
              value: _questionCount.toDouble(),
              min: 5,
              max: 50,
              divisions: 9,
              label: '$_questionCount',
              activeColor: AppColors.secondary,
              onChanged: (v) => setState(() => _questionCount = v.toInt()),
            ),
            const SizedBox(height: 16),

            // Difficulté
            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Difficulté', style: TextStyle(color: AppColors.text)),
            ),
            DropdownButtonFormField<String>(
              value: _selectedDifficulty,
              decoration: _inputDecoration(''),
              items: difficulties
                  .map((d) => DropdownMenuItem(
                        value: d,
                        child: Text(
                          d[0].toUpperCase() + d.substring(1),
                          style: const TextStyle(color: AppColors.text),
                        ),
                      ))
                  .toList(),
              onChanged: (v) => setState(() => _selectedDifficulty = v),
            ),
            const SizedBox(height: 16),

            // Catégories
            const Align(
              alignment: Alignment.centerLeft,
              child: Text('Catégories', style: TextStyle(color: AppColors.text)),
            ),
            Expanded(
              child: ListView(
                children: categoryMap.entries.map((e) {
                  final id = e.key, name = e.value;
                  return CheckboxListTile(
                    title: Text(name, style: const TextStyle(color: AppColors.text)),
                    value: _selectedCategories.contains(id),
                    activeColor: AppColors.secondary,
                    onChanged: (v) {
                      setState(() {
                        if (v == true) {
                          _selectedCategories.add(id);
                        } else {
                          _selectedCategories.remove(id);
                        }
                      });
                    },
                  );
                }).toList(),
              ),
            ),

            // Bouton démarrer
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _loading ? null : _startGame,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primary,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: _loading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text('Commencer', style: TextStyle(fontSize: 18)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
