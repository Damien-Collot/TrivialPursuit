import 'package:flutter/material.dart';
import 'package:trivia/core/constants/colors.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String? selectedMode;

  void _onModeSelected(String mode) {
    setState(() {
      selectedMode = mode;
    });
  }

  void _navigateToGame(String type) {
    // TODO: rediriger vers les pages de configuration
    print("Mode : $selectedMode - Type : $type");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.primary,
        title: const Text("QuizzChampion"),
        actions: [
          IconButton(
            icon: const Icon(Icons.account_circle, color: AppColors.text),
            onPressed: () {
              // TODO: naviguer vers profil
            },
          ),
        ],
      ),
      body: Stack(
        children: [
          Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  "Choisissez votre mode",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: AppColors.text),
                ),
                const SizedBox(height: 30),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    _buildMainButton("Solo", "🎮", () => _onModeSelected("solo")),
                    const SizedBox(width: 20),
                    _buildMainButton("Multijoueur", "🤝", () => _onModeSelected("multiplayer")),
                  ],
                ),
                if (selectedMode != null) ...[
                  const SizedBox(height: 30),
                  const Text(
                    "Choisissez un type",
                    style: TextStyle(fontSize: 18, color: AppColors.text),
                  ),
                  const SizedBox(height: 20),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _buildSubButton("Classique", "📘", () => _navigateToGame("classic")),
                      const SizedBox(width: 20),
                      _buildSubButton("Arcade", "⚡", () => _navigateToGame("arcade")),
                    ],
                  ),
                ]
              ],
            ),
          ),
          // Bottom left: paramètres
          Positioned(
            bottom: 20,
            left: 20,
            child: IconButton(
              icon: const Icon(Icons.settings, color: AppColors.text),
              onPressed: () {
                // TODO: paramètres
              },
            ),
          ),
          // Bottom right: classement
          Positioned(
            bottom: 20,
            right: 20,
            child: IconButton(
              icon: const Icon(Icons.emoji_events, color: AppColors.text),
              onPressed: () {
                // TODO: classement
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMainButton(String label, String emoji, VoidCallback onTap) {
    return ElevatedButton(
      onPressed: onTap,
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primary,
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: Column(
        children: [
          Text(emoji, style: const TextStyle(fontSize: 32)),
          const SizedBox(height: 8),
          Text(label, style: const TextStyle(fontSize: 16, color: AppColors.text)),
        ],
      ),
    );
  }

  Widget _buildSubButton(String label, String emoji, VoidCallback onTap) {
    return OutlinedButton(
      onPressed: onTap,
      style: OutlinedButton.styleFrom(
        side: const BorderSide(color: AppColors.secondary),
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: Row(
        children: [
          Text(emoji, style: const TextStyle(fontSize: 24)),
          const SizedBox(width: 8),
          Text(label, style: const TextStyle(color: AppColors.text, fontSize: 16)),
        ],
      ),
    );
  }
}
