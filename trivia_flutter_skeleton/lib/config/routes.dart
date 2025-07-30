import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../screens/login_screen.dart';
import '../screens/register_screen.dart';
import '../screens/home_screen.dart';
import '../screens/solo/solo_classic_config_screen.dart';
import '../screens/solo/solo_classic_game_screen.dart';

final router = GoRouter(
  initialLocation: '/splash',
  routes: [
    GoRoute(path: '/splash', builder: (context, state) => const SplashScreen()),
    GoRoute(path: '/', builder: (context, state) => const LoginScreen()),
    GoRoute(path: '/register', builder: (context, state) => const RegisterScreen()),
    GoRoute(path: '/home', builder: (context, state) => const HomeScreen()),
    GoRoute(
      path: '/soloClassicGame',
      builder: (context, state) {
        final args = state.extra as Map<String, dynamic>;
        return SoloClassicGameScreen(
          questionLimit: args['questionLimit'],
          difficulty: args['difficulty'],
          categories: args['categories'],
        );
      },
    ),
  ],
);
