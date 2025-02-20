## 👥 Collaborators
- **Maria Naeem** (Roll Number: 22i-0812)
- **Hamdan Sajid** (Roll Number: 22i-0872)



---



## 📂 Custom File Type - `.mh`
This programming language uses a custom file extension **`.mh`** for writing source code.



---

## 📖 Keywords
The following are the reserved keywords in this language:

```
true | false | intgr | chr | dec | boo | strg | input | output
```

---

## 🔤 Data Types
The language supports four primary data types:

1. **Boolean** (`true`, `false`) -> **boo**
2. **Integer** (Whole numbers) -> **intgr**
3. **Decimal** (Floating-point numbers) -> **dec**
4. **Character** (Single letters enclosed in `' '`) -> **chr**
5. **String** (multiple letters enclosed in `" "`) -> **strg**

---

## 🔠 Identifiers
- Variable names (identifiers) **must start with a lowercase letter (`a-z`)**.
- Identifiers can contain **letters (`a-z`), digits (`0-9`), and underscores (`_`)**.
- Example: `my_var`, `count_1`, `x`

**Regular Expression:**  
```
[a-z_][a-z0-9_]*
```

---

## 🔢 Constants
- Integer Constants: Any sequence of digits (`0-9`)
- Decimal Constants: Whole number followed by a `.` and fractional part (`3.14159`)

**Regular Expressions:**  
```
Integer: [0-9]+
Decimal: [0-9]+(\.[0-9]{1,5})?
```

> **Decimals are accurate up to five decimal places.**

---

## 🔣 Operators
The language supports basic arithmetic operations:

| Operator | Meaning |
|----------|---------|
| `+`      | Addition |
| `-`      | Subtraction |
| `*`      | Multiplication |
| `/`      | Division |
| `%`      | Modulus |

**Regular Expression:**  
```
\+|\-|\*|/|%|\^
```

---

## 🔡 Character Literals
A character must be enclosed within **single quotes (`'`)**.

**Examples:** `'A'`, `'z'`, `'#'`

**Regular Expression:**  
```
'[^']'
```

---

## 📝 String Literals
Strings must be enclosed within **double quotes (`" "`)**.

**Example:** `"Hello, world!"`

**Regular Expression:**  
```
"[^"]*"
```

---

## ✨ Features & Constraints
✅ Supports **Boolean, Integer, Decimal, and Character** types.  
✅ Only **lowercase letters (`a-z`)** are allowed for variable names.  
✅ Recognizes **arithmetic operations** (`+`, `-`, `*`, `/`, `%`).  
✅ Decimal precision **up to five decimal places**.  
✅ Ignores **extra spaces** and handles **multi-line comments** correctly.  



