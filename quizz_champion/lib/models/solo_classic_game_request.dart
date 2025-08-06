// lib/models/solo_classic_game_request.dart

class SoloClassicGameRequest {
  final int questionLimit;
  final String? difficulty;
  final List<int>? categoryFilter;

  SoloClassicGameRequest({
    required this.questionLimit,
    this.difficulty,
    this.categoryFilter,
  });

  Map<String, dynamic> toJson() => {
        // On n’inclut pas les champs null
        if (categoryFilter != null) '': null,
      };

  /// Comme les catégories et la difficulté sont passées en query params,
  /// on ne les met pas dans le body ici.
}
