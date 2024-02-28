## 적용 과정
1. 인증 서버에 애플리케이션 추가
   - redirect url은 아래 경로로 해야 한다.
     - http://localhost:8080/login/oauth2/code/google
2. 스프링 시큐리티 및 oauth2 client 의존성 추가
```kotlin
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
```
3. 인증 서버에서 발급받은 클라이언트 설정을 application.yml에 추가한다.
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: xxx
            client-secret: xxx
            scope: profile,email
```
4. 스프링 시큐리티 설정을 한다.
```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun oauth2SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
        }

        http.oauth2Login(Customizer.withDefaults())

        return http.build()
    }
}
```
5. 프론트에서 로그인을 구현한다.
- /oauth2/authorization/google 경로로 로그인
  - 위 경로는 정해진 경로임
```html
<a th:href="@{/oauth2/authorization/google}">
    <span>Sign in with Google</span>
</a>
```
6. 사용자 정보를 저장한다.
- OAuth2UserService 를 커스터마이징해야 한다.
```kotlin
@Service
@Transactional
class CustomOAuth2UserService(
  private val userRepository: UserRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
    val oAuth2User = DefaultOAuth2UserService().loadUser(userRequest)
    val userName = oAuth2User.attributes["name"].toString()
    val userEmail = oAuth2User.attributes["email"].toString()

    val user = userRepository.findByName(userName)

    if (user == null) {
      userRepository.save(
        User(
          name = userName,
          email = userEmail,
          oauth2RegistrationId = userRequest.clientRegistration.registrationId
        )
      )
    }

    return oAuth2User
  }
}
```