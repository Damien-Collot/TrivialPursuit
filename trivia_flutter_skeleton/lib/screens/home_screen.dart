import 'package:flutter/material.dart';
import 'package:trivia/core/constants/colors.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String? selectedMode; // "solo" ou "multiplayer"

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('QuizzChampion'),
        actions: [
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () {
              // TODO: Navigate to profile screen
            },
          )
        ],
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          if (selectedMode == null) ...[
            _buildMainOption("🎮 Solo", "solo"),
            const SizedBox(height: 20),
            _buildMainOption("🤝 Multijoueur", "multiplayer"),
          ] else ...[
            _buildSubMode("🚀 Arcade", selectedMode!),
            const SizedBox(height: 20),
            _buildSubMode("📚 Classique", selectedMode!),
            const SizedBox(height: 40),
            TextButton(
              onPressed: () => setState(() => selectedMode = null),
              child: const Text("⬅️ Retour", style: TextStyle(color: AppColors.text)),
            ),
          ]
        ],
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            IconButton(
              icon: const Icon(Icons.settings, color: AppColors.text),
              onPressed: () {
                // TODO: Navigate to settings screen
              },
            ),
            IconButton(
              icon: const Icon(Icons.leaderboard, color: AppColors.text),
              onPressed: () {
                // TODO: Navigate to leaderboard screen
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMainOption(String label, String mode) {
    return ElevatedButton(
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primary,
        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 20),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      onPressed: () {
        setState(() {
          selectedMode = mode;
        });
      },
      child: Text(label, style: const TextStyle(fontSize: 24, color: AppColors.text)),
    );
  }

  Widget _buildSubMode(String label, String mode) {
    return ElevatedButton(
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.secondary,
        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 20),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      onPressed: () async {
        if (mode == "solo" && label.contains("Classique")) {
          Navigator.pushNamed(context, "/solo-classic-config");
        } else {
          // TODO: Gérer les autres modes
        }
      },
      child: Text(label, style: const TextStyle(fontSize: 24, color: AppColors.text)),
    );
  }
}
