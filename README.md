# MC-86455 Fix

[English](#english) | [中文](#中文)

---

## English

### Overview

A Fabric client-side mod that fixes [MC-86455](https://bugs.mojang.com/browse/MC-86455):
**Creative mode pick block causes inventory desync between client and server.**

### The Bug

When you use pick block (middle-click) in creative mode and the target item exists in
your main inventory (slots 9–35), the client swaps the item with your current hotbar
item, but **only syncs the hotbar slot** to the server.  The server never learns about
the moved item, causing:

- **Item duplication** — the picked item exists in both the hotbar and the inventory
  slot on the server
- **Item loss** — the old hotbar item that was swapped into the inventory disappears
  from the server's view

### The Fix

This mod ensures that **every inventory slot modified by pick block is properly
synced** to the server, eliminating the desync entirely.

### Supported Versions

| Minecraft | Status |
|-----------|--------|
| 1.17.1    | ✅ |
| 1.18.2    | ✅ |
| 1.19.4    | ✅ |
| 1.20.1    | ✅ |
| 1.20.6    | ✅ |

> **Note:** The bug is fixed in vanilla as of the 1.21.4 snapshots (via server-side
> pick block). This mod backports the fix to older versions.

### Installation

1. Install [Fabric Loader](https://fabricmc.net/) for your Minecraft version.
2. Download the mod JAR matching your Minecraft version from
   [Releases](https://github.com/akio/mc86455-fix/releases).
3. Place the JAR in your `mods/` folder.
4. **This is a client-only mod.** It does not need to be installed on the server.

### Building

```bash
# Build all versions
./gradlew build

# Build a specific version
./gradlew :1.20.1:build
```

Built JARs are placed in `{version}/build/libs/`.

#### Build requirements

- JDK 17 (1.17.1 – 1.20.1) / JDK 21 (1.20.6)

A Gradle wrapper is included — no Gradle installation is needed.

---

## 中文

### 概述

一个 Fabric 客户端 Mod，修复 [MC-86455](https://bugs.mojang.com/browse/MC-86455)：
**创造模式使用"选取方块"（中键）导致客户端与服务端背包不同步。**

### Bug 描述

在创造模式下，当你对已存在于背包（9–35 号槽位）中的方块使用选取方块时，
客户端会将快捷栏物品与该背包物品**交换**，但只把**快捷栏的变更**发送给了
服务端。服务端对背包槽位的变化一无所知，导致：

- **物品复制** — 服务端上目标物品同时存在于快捷栏和背包中
- **物品丢失** — 原先快捷栏中被交换到背包的物品在服务端"消失"了

### 修复方式

本 Mod 确保**被选取方块操作修改过的所有背包槽位**都正确同步到服务端，
彻底消除客户端与服务端的不一致。

### 支持的版本

| Minecraft | 状态 |
|-----------|------|
| 1.17.1    | ✅ |
| 1.18.2    | ✅ |
| 1.19.4    | ✅ |
| 1.20.1    | ✅ |
| 1.20.6    | ✅ |

> **注意：** 1.21.4 的快照中 Mojang 已在服务端层面修复了此 Bug（将选取方块逻辑
> 完全移到服务端执行）。本 Mod 将此修复向下移植到旧版本。

### 安装

1. 安装对应版本的 [Fabric Loader](https://fabricmc.cn/)。
2. 从 [Releases](https://github.com/akio/mc86455-fix/releases) 下载对应版本的 JAR。
3. 放入 `mods/` 文件夹。
4. **本 Mod 仅需客户端安装**，服务端无需安装。

### 构建

```bash
# 构建所有版本
./gradlew build

# 构建特定版本
./gradlew :1.20.1:build
```

构建产物在 `{version}/build/libs/` 目录下。

#### 构建要求

- JDK 17（1.17.1 – 1.20.1）/ JDK 21（1.20.6）

项目中已包含 Gradle Wrapper，无需额外安装 Gradle。

---

### License

WTFPL Version 2
