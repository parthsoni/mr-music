package org.badger.mr.music.download;

import org.mult.daap.client.Song;

public class DownloadSong extends Song {
	public int progress;
	public int status;
	public static int STATUS_COMPLETE = 0;
	public static int STATUS_INPROGRESS = 1;
	public static int STATUS_CANCELLED = 2;
	public static int STATUS_NOTSTARTED = 3;
	public static int STATUS_PAUSED = 4;
	public static int STATUS_ERROR = 4;
	
	public DownloadSong() {
		super();
		status = STATUS_NOTSTARTED;
		progress = 0;
	}
	
	public static DownloadSong toDownloadSong(Song s)
	{
		DownloadSong dls = new DownloadSong();
		dls.album = s.album;
		dls.artist = s.artist;
		dls.status = STATUS_NOTSTARTED;
		dls.disc_num = s.disc_num;
		dls.format = s.format;
		dls.genre = s.genre;
		dls.host = s.host;
		dls.id = s.id;
		dls.isLocal = s.isLocal;
		dls.localPath = s.localPath;
		dls.name = s.name;
		dls.progress = 0;
		dls.size = s.size;
		dls.time = s.time;
		dls.track = s.track;
		 
		return dls;
		
	}

}
