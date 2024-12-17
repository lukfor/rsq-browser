var recentUploadsString = localStorage.getItem('recent_uploads');
var recentUploads = [];

// Parse existing recent uploads if available
if (recentUploadsString != undefined) {
  recentUploads = JSON.parse(recentUploadsString);
}

var jobId = '{{report.getId()}}'; // Current job ID
var createdOn = new Date().toISOString(); // Current date/time in ISO format

// Check if the job ID is already present
var jobExists = recentUploads.some((job) => job.id === jobId);

if (!jobExists) {
  // Add new job object to the recent uploads
  recentUploads.push({
    id: jobId,
    createdOn: createdOn
  });

  // Save back to localStorage
  localStorage.setItem('recent_uploads', JSON.stringify(recentUploads));
}