import 'package:flutter/material.dart';
import '../../../core/widgets/custom_button.dart';
import '../../../core/constants/colors.dart';
import 'package:go_router/go_router.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Text('Connexion', style: TextStyle(fontSize: 24)),
              const SizedBox(height: 20),
              const TextField(decoration: InputDecoration(hintText: 'Email')),
              const SizedBox(height: 12),
              const TextField(decoration: InputDecoration(hintText: 'Mot de passe'), obscureText: true),
              const SizedBox(height: 24),
              CustomButton(text: 'Se connecter', onPressed: () => context.go('/home')),
              TextButton(
                onPressed: () => context.go('/register'),
                child: const Text("Pas encore de compte ? S'inscrire"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
