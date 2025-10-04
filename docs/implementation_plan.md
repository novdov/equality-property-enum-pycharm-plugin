# EqualityPropertyEnum PyCharm Plugin 구현 계획

## 프로젝트 개요

### 목적
`EqualityPropertyEnum`을 상속한 Python Enum 클래스에 동적으로 생성되는 `is_*` 프로퍼티에 대한 IDE 자동완성 및 타입 힌트 제공

### 핵심 기능
- `Status.FINISHED.is_finished` 형태의 프로퍼티 자동완성
- `is_*` 프로퍼티의 타입이 `bool`임을 PyCharm에 인식
- 동적 프로퍼티에 대한 정적 분석 지원

### 동작 규칙
1. **프로퍼티 생성**: 모든 Enum 멤버에 대해 `is_*` 프로퍼티 생성
2. **이름 변환**: `MULTI_WORD_STATUS` → `is_multi_word_status` (소문자 변환, snake_case 유지)
3. **반환 값**: `is_{member.value} = True` (현재 enum의 값이 member.value인지 검사)

### 예시
```python
class Status(EqualityPropertyEnum):
    FINISHED = auto()
    PENDING = auto()
    IN_PROGRESS = auto()

status = Status.FINISHED
status.is_finished      # True (bool 타입으로 인식)
status.is_pending       # False
status.is_in_progress   # False (자동완성 제공)
```

## 기술 스택

### 개발 환경
- **언어**: Kotlin 2.2.20
- **빌드 도구**: Gradle + IntelliJ Platform Plugin 2.1.0
- **타겟 IDE**: PyCharm 2025.1+ (Community/Professional)
- **필수 플러그인**: PythonCore (bundled)

## 구현 아키텍처

### 핵심 컴포넌트

#### 1. Type Provider
- **클래스**: `EqualityPropertyEnumTypeProvider`
- **Extension Point**: `Pythonid.typeProvider`
- **역할**: `is_*` 프로퍼티의 타입을 `bool`로 제공
- **동작 조건**:
  - 참조 표현식이 `is_*` 패턴
  - Qualifier가 `EqualityPropertyEnum` 상속 클래스의 멤버
  - 유효한 Enum 멤버 이름인지 검증

#### 2. Completion Contributor
- **클래스**: `EqualityPropertyEnumCompletionContributor`
- **Extension Point**: `com.intellij.completion.contributor`
- **역할**: `Status.FINISHED.` 입력 시 `is_finished` 자동완성 제안
- **동작 조건**:
  - Qualifier 타입이 `EqualityPropertyEnum` 상속 클래스
  - 모든 Enum 멤버에 대해 `is_*` 프로퍼티 생성
  - Property 아이콘, bool 타입 표시

#### 3. Inspection
- **클래스**: `InvalidEqualityPropertyAccessInspection`
- **Extension Point**: `com.intellij.localInspection`
- **역할**: 존재하지 않는 `is_*` 프로퍼티 접근 경고
- **동작 조건**:
  - `EqualityPropertyEnum` 상속 클래스에서 `is_*` 접근 시
  - 해당하는 Enum 멤버가 없으면 경고 표시
  - 사용 가능한 프로퍼티 목록 제안

## 구현 상태

### 완료된 기능
1. ✅ **프로젝트 설정**
   - build.gradle.kts, plugin.xml 구성
   - PyCharm 2025.1 호환

2. ✅ **테스트 인프라**
   - EqualityPropertyEnum.py 작성
   - 테스트 데이터 준비 (test_type.py, test_completion.py)

3. ✅ **Type Provider 구현**
   - bool 타입 제공
   - EqualityPropertyEnum 상속 클래스 감지

4. ✅ **Completion Contributor 구현**
   - 자동완성 제안
   - 동적 프로퍼티 생성 로직

5. ✅ **Inspection 구현**
   - 잘못된 is_* 프로퍼티 접근 경고
   - 사용 가능한 프로퍼티 제안

### 향후 개선 사항 (Phase 2)
- 🔮 **Goto Definition 지원**: `is_finished` → `FINISHED` 멤버로 이동
- 🔮 **Documentation 제공**: Hover 시 동적 프로퍼티 설명 표시
- 🔮 **Refactoring 지원**: Enum 멤버 이름 변경 시 `is_*` 프로퍼티도 자동 업데이트

## 핵심 개념

### PyTypeProvider
- **역할**: Python 표현식의 타입 정보 제공
- **주요 메서드**:
  - `getReferenceType()`: 참조 표현식의 타입 반환
  - `getCallType()`: 호출 표현식의 타입 반환
- **등록**: `Pythonid.typeProvider` Extension Point

### CompletionContributor
- **역할**: 코드 자동완성 제안 추가
- **구현 방법**:
  1. `CompletionContributor` 상속
  2. `extend()` 메서드에서 Pattern과 Provider 등록
  3. `CompletionProvider` 구현하여 `addCompletions()` 작성
- **등록**: `com.intellij.completion.contributor` Extension Point

### PyInspection
- **역할**: 코드 품질 검사 및 경고 제공
- **구현 방법**:
  1. `PyInspection` 상속
  2. `buildVisitor()` 메서드에서 `PyInspectionVisitor` 구현
  3. 문제 발견 시 `registerProblem()` 호출
- **등록**: `com.intellij.localInspection` Extension Point

### TypeEvalContext
- **codeAnalysis**: 코드 분석용 (Type Provider, Inspection에서 사용)
- **codeCompletion**: 자동완성용 (Completion Contributor에서 사용)
- **주의**: 적절한 컨텍스트 모드 선택 중요

## 참고 자료

### 공식 문서
- [Plugin Development Welcome](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [PyCharm Plugin Development](https://plugins.jetbrains.com/docs/intellij/pycharm.html)
- [Code Completion](https://plugins.jetbrains.com/docs/intellij/code-completion.html)
- [Python Plugin Extension Points](https://plugins.jetbrains.com/docs/intellij/python.html)
- [Extension Point List](https://plugins.jetbrains.com/docs/intellij/intellij-community-plugins-extension-point-list.html)

### 참고 프로젝트
- [Pydantic PyCharm Plugin](https://github.com/koxudaxi/pydantic-pycharm-plugin)
  - BaseModel 동적 프로퍼티 지원 플러그인
  - 타입 검증 및 Inspection 구현 참고
- [Flake8 Support](https://plugins.jetbrains.com/plugin/11563-flake8-support)
- [Python Typing Imp](https://plugins.jetbrains.com/plugin/19440-python-typing-imp)

### 블로그 & 튜토리얼
- [Writing PyCharm Plugin](http://kflu.github.io/2019/08/24/2019-08-24-writing-pycharm-plugin/)

## 문제 해결 가이드

### 1. Python Plugin 의존성 문제
- PyCharm Community: `PythonCore`
- PyCharm Professional: `Pythonid`

### 2. 타입 추론이 동작하지 않는 경우
- TypeEvalContext 모드 확인 (codeAnalysis vs codeCompletion)
- Qualifier 타입 검증 로직 재확인

### 3. 자동완성이 표시되지 않는 경우
- Pattern 매칭 로직 확인
- Completion Contributor 등록 확인
- plugin.xml의 extension point 확인

### 4. Inspection이 동작하지 않는 경우
- plugin.xml에서 localInspection 등록 확인
- TypeEvalContext.codeAnalysis 사용 확인
- 방문자 패턴(Visitor) 구현 확인
