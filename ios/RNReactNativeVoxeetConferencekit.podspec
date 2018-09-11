
Pod::Spec.new do |s|
  s.name         = "RNReactNativeVoxeetConferencekit"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeVoxeetConferencekit"
  s.description  = <<-DESC
                  RNReactNativeVoxeetConferencekit
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeVoxeetConferencekit.git", :tag => "master" }
  s.source_files  = "RNReactNativeVoxeetConferencekit/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  