# Mario Pose Reference

This document defines the intended body pose for each shared Mario sprite type.
The pose names are shared across all four forms/states:

- small Mario
- big Mario
- fire Mario
- star-man Mario

The filenames can vary by prefix, but the pose meanings should stay consistent.

## Shared Pose Types

### `idle`
- Mario is standing upright in a neutral stance.
- Head faces forward or slightly in the facing direction.
- Arms are relaxed.
- Feet are planted under the body.
- No leaning, no jump, no skid motion.

### `run1`
- First running contact pose.
- Front leg reaches forward.
- Back leg pushes off behind.
- Arms counter-swing opposite the legs.
- Body leans slightly into movement.

### `run2`
- Mid-stride transition pose.
- Legs pass closer under the torso.
- Arms transition through the middle of the swing.
- This frame should feel like the center beat of the run cycle.

### `run3`
- Opposite running contact pose from `run1`.
- The leg positions are mirrored in function relative to `run1`.
- Arms should also counter-swing opposite `run1`.
- Body still leans slightly into movement.

### `jump`
- Mario is airborne.
- Knees bend upward or legs tuck slightly.
- Arms lift or brace for air control.
- Torso is compact compared with running poses.
- This pose should read clearly as “not grounded”.

### `skid`
- Mario is stopping while moving in the opposite direction.
- Torso leans backward against motion.
- Front foot braces hard against the ground.
- Rear foot trails behind.
- Arms help balance the braking motion.
- This pose should communicate friction and loss of momentum.

### `crouch`
- Used only for big Mario and fire Mario.
- Mario lowers his body vertically while staying in place.
- Head drops into the shoulders or hat brim lowers.
- Knees bend sharply.
- Torso compresses downward rather than leaning forward.

## Form Notes

### Small Mario
- Uses: `idle`, `run1`, `run2`, `run3`, `jump`, `skid`
- Does not need `crouch`
- Silhouette should remain compact and readable at 1x1 unit

### Big Mario
- Uses: `idle`, `run1`, `run2`, `run3`, `jump`, `skid`, `crouch`
- Same pose intent as small Mario, but extended vertically
- Limb spacing should reflect the taller body

### Fire Mario
- Uses: `idle`, `run1`, `run2`, `run3`, `jump`, `skid`, `crouch`
- Same body posing as big Mario
- Differences should come from palette, suit pattern, and fire-form identity

### Star-Man Mario
- Uses the same base pose set as the current underlying form:
  `idle`, `run1`, `run2`, `run3`, `jump`, `skid`, and `crouch` when the base form supports it
- Star-Man is a state variant, not a separate body-plan variant
- The main visual difference should come from palette cycling, highlight treatment, glow, or other invincibility indicators
- Pose silhouettes should remain consistent with the matching small, big, or fire form underneath

## Current Placeholder Naming

### Small
- `SI_small_idle_1x1.png`
- `S1_small_run1_1x1.png`
- `S2_small_run2_1x1.png`
- `S3_small_run3_1x1.png`
- `SJ_small_jump_1x1.png`
- `SK_small_skid_1x1.png`

### Big
- `BI_big_idle_1x2.png`
- `B1_big_run1_1x2.png`
- `B2_big_run2_1x2.png`
- `B3_big_run3_1x2.png`
- `BJ_big_jump_1x2.png`
- `BK_big_skid_1x2.png`
- `BC_big_crouch_1x2.png`

### Fire
- `PI_fire_idle_1x2.png`
- `P1_fire_run1_1x2.png`
- `P2_fire_run2_1x2.png`
- `P3_fire_run3_1x2.png`
- `PJ_fire_jump_1x2.png`
- `PK_fire_skid_1x2.png`
- `PC_fire_crouch_1x2.png`

### Star-Man
- No separate placeholder filenames are required if the effect is implemented as palette cycling or overlay on top of the current base-form sprites
- If dedicated placeholders are added later, they should mirror the same pose naming as the underlying form and include a clear `star` or `starman` marker in the filename
