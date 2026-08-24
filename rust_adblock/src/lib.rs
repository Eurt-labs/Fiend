use adblock::engine::Engine;
use adblock::request::Request;
use jni::objects::{JClass, JString};
use jni::sys::jboolean;
use jni::JNIEnv;

thread_local! {
    static ENGINE: Engine = {
        // Initialize with empty rules or proper EasyList rules later.
        Engine::new_with_list_text("")
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
    
    // Construct the adblock Request object safely
    let request = match Request::new(&url_str, "https://music.youtube.com", &res_type_str, "GET") {
        Ok(r) => r,
        Err(_) => return 0, // If URL parsing fails, don't block
    };
    
    // Check if blocked using the thread-local engine
    let blocked = ENGINE.with(|engine| {
        let result = engine.check_network_request(&request);
        // It's blocked if it matched a block filter and didn't match an exception
        result.filter.is_some() && result.exception.is_none()
    });
    
    if blocked {
        1
    } else {
        0
    }
}
