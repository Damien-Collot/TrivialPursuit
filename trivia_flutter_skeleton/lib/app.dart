import 'package:flutter/material.dart';
import 'config/routes.dart';

class TriviaApp extends StatelessWidget {
  const TriviaApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Trivia Game',
      theme: ThemeData.dark(useMaterial3: true),
      routerConfig: router,
    );
  }
}
