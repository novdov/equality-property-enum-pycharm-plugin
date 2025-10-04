# EqualityPropertyEnum PyCharm Plugin Specification

## 프로젝트 개요

### 목적
`EqualityPropertyEnum`을 상속한 Python Enum 클래스에 동적으로 생성되는 `is_*` 프로퍼티에 대한 IDE 자동완성 및 타입 힌트 제공

### 배경
Python에서 Enum 클래스에 `__getattr__`을 사용해 동적 프로퍼티를 생성하는 경우, PyCharm이 이를 정적 분석으로 인식하지 못해 자동완성이 제공되지 않음

### 목표
- `Status.FINISHED.is_finished` 형태의 프로퍼티 자동완성
- `is_finished`의 타입이 `bool`임을 PyCharm에 인식시킴
- Pydantic 플러그인과 유사한 사용자 경험 제공

## 기술 스펙

### 개발 환경
- **언어**: Kotlin 1.9.20
- **빌드 도구**: Gradle + IntelliJ Platform Plugin 2.1.0
- **타겟 IDE**: PyCharm 2025.1+ (Community/Professional)
- **필수 플러그인**: PythonCore (bundled)

## 기능 명세

### 1. 타입 제공 (Type Provider)

**클래스**: `EqualityPropertyEnumTypeProvider`
**역할**: 동적 프로퍼티의 타입을 `bool`로 제공

#### 동작 조건
1. 참조 표현식이 `is_*` 형태
2. qualifier가 `EqualityPropertyEnum` 상속 클래스의 멤버
3. qualifier가 유효한 Enum 멤버 (대문자로 정의)

#### 예시
```python
class Status(EqualityPropertyEnum):
    FINISHED = auto()
    PENDING = auto()

status = Status.FINISHED
status.is_finished  # <- 여기서 bool 타입 제공
```

#### 구현 요구사항
- `PyTypeProviderBase` 상속
- `getReferenceType()` 메서드 구현
- `PyBuiltinCache.getInstance().boolType` 반환

---

### 2. 자동완성 (Code Completion)

**클래스**: `EqualityPropertyEnumCompletionContributor`
**역할**: `Status.FINISHED.` 입력 시 `is_finished` 제안

#### 동작 조건
1. qualifier 타입이 `EqualityPropertyEnum` 상속 클래스
2. qualifier가 유효한 Enum 멤버
3. 멤버 이름에 해당하는 `is_*` 프로퍼티만 제안

#### 자동완성 항목 속성
- **이름**: `is_{member_name.lowercase()}`
- **타입**: `bool`
- **아이콘**: Property 아이콘
- **꼬리 텍스트**: "(dynamic property)"
- **괄호 삽입**: 없음 (프로퍼티이므로)

#### 예시
```python
Status.FINISHED.  # <- 여기서 자동완성 트리거
# 제안: is_finished (bool)
```
