class UserModel {
  const UserModel({
    required this.id,
    required this.name,
    required this.email,
    required this.username,
    required this.role,
    this.noHp,
    this.photo,
    this.token = '',
  });

  final int? id;
  final String name;
  final String email;
  final String username;
  final String role;
  final String? noHp;
  final String? photo;
  final String token;

  bool get isAdmin => role.toLowerCase() == 'admin';
  bool get isCashier => role.toLowerCase() == 'kasir';
  bool get isCustomer => role.toLowerCase() == 'customer';

  factory UserModel.fromApiJson(Map<String, dynamic> json, {String token = ''}) {
    final responseData = json['data'] is Map
        ? Map<String, dynamic>.from(json['data'] as Map)
        : json;
    final userJson = responseData['user'] is Map
        ? Map<String, dynamic>.from(responseData['user'] as Map)
        : responseData;
    return UserModel(
      id: userJson['id'] is int ? userJson['id'] : int.tryParse('${userJson['id']}'),
      name: (userJson['name'] ?? '').toString(),
      email: (userJson['email'] ?? '').toString(),
      username: (userJson['username'] ?? userJson['name'] ?? '').toString(),
      role: (userJson['role'] ?? '').toString(),
      noHp: userJson['no_hp']?.toString(),
      photo: userJson['photo']?.toString(),
      token: token.isNotEmpty
          ? token
          : (responseData['token'] ?? json['token'] ?? '').toString(),
    );
  }

  factory UserModel.fromLocalMap(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] as int?,
      name: (json['name'] ?? '').toString(),
      email: (json['email'] ?? '').toString(),
      username: (json['username'] ?? '').toString(),
      role: (json['role'] ?? '').toString(),
      noHp: json['no_hp']?.toString(),
      photo: json['photo']?.toString(),
      token: (json['token'] ?? '').toString(),
    );
  }

  Map<String, dynamic> toLocalMap() {
    return {
      'id': id,
      'name': name,
      'email': email,
      'username': username,
      'role': role,
      'no_hp': noHp,
      'photo': photo,
      'token': token,
    };
  }
}
