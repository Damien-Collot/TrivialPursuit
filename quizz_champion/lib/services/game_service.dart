import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class GameService {
  final String baseUrl = "http://<TON-IP>:8080/api/sessions"; // Remplace <TON-IP>

  Future<String?> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString("jwt");
  }

  Future<Map<String, dynamic>?> startSoloClassicSession({
    required int userId,
    required int questionLimit,
    String? difficulty,
    List<int>? categories,
  }) async {
    final token = await _getToken();
    final response = await http.post(
      Uri.parse("$baseUrl/solo/classic?userId=$userId&questionLimit=$questionLimit${difficulty != null ? "&difficulty=$difficulty" : ""}"),
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer $token"
      },
      body: jsonEncode(categories ?? []),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      print("Erreur création session: ${response.body}");
      return null;
    }
  }

  Future<Map<String, dynamic>?> fetchNextQuestion(int sessionId) async {
    final token = await _getToken();
    final response = await http.get(
      Uri.parse("$baseUrl/$sessionId/question"),
      headers: {
        "Authorization": "Bearer $token"
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      print("Erreur fetch question: ${response.body}");
      return null;
    }
  }

  Future<Map<String, dynamic>?> submitAnswer({
    required int sessionId,
    required String userAnswer,
  }) async {
    final token = await _getToken();
    final response = await http.post(
      Uri.parse("$baseUrl/$sessionId/answer?userAnswer=${Uri.encodeComponent(userAnswer)}"),
      headers: {
        "Authorization": "Bearer $token"
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      print("Erreur soumission réponse: ${response.body}");
      return null;
    }
  }
}
