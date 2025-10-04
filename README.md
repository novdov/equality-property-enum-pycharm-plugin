# EqualityPropertyEnum Support

A PyCharm plugin that provides intelligent IDE support for dynamic properties in Python Enum classes that inherit from `EqualityPropertyEnum`.

## Overview

When working with Python Enums that use `__getattr__` to dynamically generate `is_*` properties, PyCharm's static analysis cannot recognize these properties, resulting in missing autocomplete and type hints. This plugin solves that problem by providing:

- **Smart Autocomplete**: Suggests `is_*` properties for enum members
- **Type Inference**: Recognizes dynamic properties as `bool` type
- **Code Validation**: Warns about invalid property access with helpful suggestions

## Features

### 1. Dynamic Property Autocomplete

When you type `Status.FINISHED.`, the plugin automatically suggests all valid `is_*` properties based on the enum's members.

```python
class Status(EqualityPropertyEnum):
    FINISHED = auto()
    PENDING = auto()
    IN_PROGRESS = auto()

status = Status.FINISHED
status.is_  # Autocomplete suggests: is_finished, is_pending, is_in_progress
```

### 2. Type Inference

The plugin informs PyCharm that `is_*` properties return `bool`, enabling proper type checking and IDE features.

```python
result: bool = Status.FINISHED.is_finished  # Type checks correctly
```

### 3. Invalid Property Detection

Get real-time warnings when accessing non-existent properties, with suggestions for valid alternatives.

```python
status.is_invalid  # Warning: EqualityPropertyEnum member 'Status' has no property 'is_invalid'.
                   # Available: is_finished, is_pending, is_in_progress
```

## Installation

### From Source

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/enum-property-plugin.git
   cd enum-property-plugin
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install the plugin in PyCharm:
   - Go to `Settings` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
   - Select `build/distributions/enum-property-plugin-1.0.0.zip`
   - Restart PyCharm

## Requirements

- **PyCharm**: 2025.1 or later (Community or Professional)
- **Python Plugin**: Bundled with PyCharm
- **JDK**: 21 or later (for building from source)

## How It Works

The plugin consists of three main components:

1. **Type Provider** (`EqualityPropertyEnumTypeProvider`): Informs PyCharm that `is_*` properties return `bool` type
2. **Completion Contributor** (`EqualityPropertyEnumCompletionContributor`): Provides autocomplete suggestions for valid `is_*` properties
3. **Code Inspection** (`InvalidEqualityPropertyAccessInspection`): Validates property access and suggests corrections

The plugin detects classes that inherit from `EqualityPropertyEnum` and analyzes their enum members (uppercase class attributes) to generate appropriate `is_*` property names using the pattern: `is_{member_name.lowercase()}`.

## Development

### Build

```bash
./gradlew build
```

### Run in Development IDE

```bash
./gradlew runIde
```

### Run Tests

```bash
./gradlew test
```

## Technical Stack

- **Language**: Kotlin 2.2.20
- **Build Tool**: Gradle
- **Framework**: IntelliJ Platform Plugin SDK 2.9.0
- **Target Platform**: PyCharm 2025.1+

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

novdov