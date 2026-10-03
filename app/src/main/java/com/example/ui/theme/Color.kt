package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Base44 Exact Design Tokens (Measured from current app screenshots)
val BasePageBg = Color(0xFF020617) // slate-950
val BaseSurface = Color(0xFF0A1022) // sidebar, cards
val BaseRaisedSurface = Color(0xFF0F1322) // header search, day tiles
val BaseTileChip = Color(0xFF101627) // chips, visit items
val BaseModalBg = Color(0xFF0F172A) // modal background (slate-900)
val BaseModalInput = Color(0xFF1B2335) // modal inputs
val BaseBorder = Color(0xFF171C2E) // 1px borders on cards and sidebar edge
val BaseBorderHeader = Color(0xFF0F1322) // header bottom edge
val BaseBorderInput = Color(0xFF334155) // 1px slate-700 border

val BaseTextHeading = Color(0xFFFFFFFF)
val BaseTextBody = Color(0xFFCBD5E1)
val BaseTextMuted = Color(0xFF94A3B8) // slate-400
val BaseTextFaint = Color(0xFF64748B) // slate-500

val BaseEmerald500 = Color(0xFF10B981) // primary button
val BaseEmerald600 = Color(0xFF059669) // button hover
val BaseEmerald300 = Color(0xFF6EE7B7) // active nav text, status low
val BaseEmerald400 = Color(0xFF34D399) // links, completed visit dot
val BaseNavActiveBg = Color(0xFF102C32) // active nav background ~15%

// Brand gradient: emerald to teal (#2BC595 to #17A48D at 135 deg)
val BrandGradientStart = Color(0xFF2BC595)
val BrandGradientEnd = Color(0xFF17A48D)
val BrandGradient = Brush.linearGradient(listOf(BrandGradientStart, BrandGradientEnd))

// AI Accent gradient: violet to fuchsia (#8B5CF6 to #D946EF)
val AiGradientStart = Color(0xFF8B5CF6)
val AiGradientEnd = Color(0xFFD946EF)
val AiGradient = Brush.linearGradient(listOf(AiGradientStart, AiGradientEnd))
val AiChipText = Color(0xFFC4B5FD)
val AiChipBg = Color(0xFF1E1C43)

// Status colors (text -300, 10-15% opacity bg, 1px border)
val StatusLowText = Color(0xFF6EE7B7)
val StatusLowBg = Color(0x2010B981)
val StatusLowBorder = Color(0x4010B981)

val StatusMedText = Color(0xFFFCD34D)
val StatusMedBg = Color(0x20F59E0B)
val StatusMedBorder = Color(0x40F59E0B)

val StatusHighText = Color(0xFFFDBA74)
val StatusHighBg = Color(0x20F97316)
val StatusHighBorder = Color(0x40F97316)

val StatusCritText = Color(0xFFFDA4AF)
val StatusCritBg = Color(0x20F43F5E)
val StatusCritBorder = Color(0x40F43F5E)

val StatusInfoText = Color(0xFF7DD3FC)
val StatusInfoBg = Color(0x200284C7)
val StatusInfoBorder = Color(0x400284C7)

val StatusVioletText = Color(0xFFC4B5FD)
val StatusVioletBg = Color(0x208B5CF6)
val StatusVioletBorder = Color(0x408B5CF6)

// Visit status dots
val DotScheduled = Color(0xFF64748B) // slate-500
val DotInProgress = Color(0xFF38BDF8) // sky-400
val DotCompleted = Color(0xFF34D399) // emerald-400
val DotMissed = Color(0xFFFBBF24) // amber-400
val DotCancelled = Color(0xFFFB7185) // rose-400

// Legacy aliases for compatibility
val CareNavyDark = BasePageBg
val CareNavySurface = BaseSurface
val CareNavySurfaceElevated = BaseRaisedSurface
val CareNavyBorder = BaseBorder
val CareNavyContainer = BaseTileChip

val CareMintTeal = BaseEmerald500
val CareMintTealLight = BaseEmerald400
val CareMintTealDark = BaseEmerald600
val CareMintTealContainer = BaseNavActiveBg
val CareMintTealOnContainer = BaseEmerald300

val CareCyan = StatusInfoText
val CareAmber = StatusMedText
val CareRed = StatusCritText
val CarePurple = StatusVioletText

val TextPrimaryDark = BaseTextHeading
val TextSecondaryDark = BaseTextMuted
val TextMutedDark = BaseTextFaint

