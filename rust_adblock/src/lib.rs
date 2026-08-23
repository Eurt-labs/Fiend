use adblock::engine::Engine;
use jni::objects::{JClass, JString};
use jni::sys::jboolean;
use jni::JNIEnv;

// Simple global engine for demo purposes. In a real app, this should be initialized with EasyList.
lazy_static::lazy_static! {
    static ref ENGINE: Engine = {
        let mut engine = Engine::new(true);
        // Load some default rules or load from file
        // engine.deserialize(...) 
        engine
    };
}

#[no_mangle]
pub extern "system" fn Java_com_example_fiend_AdblockEngine_shouldBlock(
    mut env: JNIEnv,
    _class: JClass,
    url: JString,
    resource_type: JString,
) -> jboolean {
    let url_str: String = env.get_string(&url).expect("Couldn't get java string!").into();
    let res_type_str: String = env.get_string(&resource_type).expect("Couldn't get java string!").into();
    
    // Simplistic check for testing. Replace with proper ENGINE.check_network_request() later.
    let blocked = ENGINE.check_network_request(&url_str, "https://music.youtube.com", &res_type_str).matched;
    
    if blocked {
        1
    } else {
        0
    }
}
