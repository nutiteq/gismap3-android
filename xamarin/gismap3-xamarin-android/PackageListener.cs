﻿
using Nutiteq.PackageManager;
using Nutiteq.Utils;

namespace HelloMap
{

	public class PackageListener : PackageManagerListener
	{
		private PackageManager _packageManager;

		public PackageListener (PackageManager packageManager)
		{
			_packageManager = packageManager;
		}

		public override void OnPackageListUpdated ()
		{
			// called when package list is downloaded
			// now you can start downloading packages
			Log.Debug ("OnPackageListUpdated");

		}

		public override void OnPackageListFailed ()
		{
			Log.Debug ("OnPackageListFailed");
			// Failed to download package list
		}

		public override void OnPackageStatusChanged (string id, int version, PackageStatus status)
		{
			// a portion of package is downloaded. Update your progress bar here.
			// Notice that the view and SDK are in different threads, so data copy id needed
			Log.Debug ("OnPackageStatusChanged " + id + " ver " + version + " progress " + status.Progress);
		}

		public override void OnPackageCancelled (string id, int version)
		{
			// called when you called cancel package download
			Log.Debug ("OnPackageCancelled");
		}

		public override void OnPackageUpdated (string id, int version)
		{
			// called when package is updated
			Log.Debug ("OnPackageUpdated");
		}

		public override void OnPackageFailed (string id, int version, PackageErrorType errorType)
		{
			// Failed to download package " + id + "/" + version
			Log.Debug ("OnPackageFailed: " + errorType);
		}
	}
}