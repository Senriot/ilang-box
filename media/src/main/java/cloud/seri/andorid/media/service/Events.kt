package cloud.seri.andorid.media.service

import cloud.seri.andorid.media.model.Song


class ChangePlaylistEvent(val type: PlayListChangeType, val item: Song)
class PlaylistChanged

enum class PlayListChangeType {
    ADD,
    DELETE,
    TOP
}